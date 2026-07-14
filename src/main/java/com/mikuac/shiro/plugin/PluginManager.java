package com.mikuac.shiro.plugin;

import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.properties.ShiroProperties;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PluginManager implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {

    /**
     * 从 Environment 直接读取（非从 ShiroProperties），避免 getBean 触发 ShiroProperties 在
     * ConfigurationPropertiesBindingPostProcessor 注册前被提前创建， 导致 YAML 绑定永不到来。
     */
    private String pluginScanPath;
    private DependencyResolver dependencyResolver;
    private ApplicationContext applicationContext;
    private List<URL> pluginUrls = List.of();

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Environment env = applicationContext.getEnvironment();
        this.pluginScanPath = env.getProperty("shiro.plugin-scan-path", "plugins");
        String mavenRepoUrl = env.getProperty("shiro.plugin-maven-repository-url",
                "https://maven.aliyun.com/repository/public");
        this.dependencyResolver = new DependencyResolver(mavenRepoUrl);
    }

    // ==================== BeanDefinitionRegistryPostProcessor ====================
    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

        List<URL> urls;
        try {
            urls = collectAllPluginUrls();
            pluginUrls = List.copyOf(urls);

            if (urls.isEmpty()) {
                log.warn("No external plugin JARs detected");
                return;
            }

            try (URLClassLoader pluginClassLoader = new URLClassLoader(
                    urls.toArray(URL[]::new),
                    originalClassLoader
            )) {
                if (registry instanceof DefaultListableBeanFactory beanFactory) {
                    beanFactory.setBeanClassLoader(pluginClassLoader);
                }

                withPluginClassLoader(pluginClassLoader, () -> {
                    log.info("Plugin ClassLoader initialized");
                    try {
                        scanAndRegister(registry, pluginClassLoader);
                    } catch (Exception e) {
                        log.error("Plugin BeanDefinition registration failed", e);
                    }
                });
            }

            // registerBotPlugins 延后到 onApplicationEvent(ContextRefreshedEvent)
            // 因为此时 ShiroProperties 的 @ConfigurationProperties 绑定还未发生
        } catch (Exception e) {
            log.error("Plugin BeanDefinition registration failed", e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            if (registry instanceof DefaultListableBeanFactory beanFactory) {
                beanFactory.setBeanClassLoader(originalClassLoader);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) {
        // No-op: plugin bean definitions are registered in postProcessBeanDefinitionRegistry.
    }

    // ==================== ApplicationListener<ContextRefreshedEvent> ====================
    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        // 没有外部插件 JAR 时跳过，但依然打印汇总（显示 YAML 插件）
        if (pluginUrls.isEmpty()) {
            printPluginSummary();
            return;
        }

        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try (URLClassLoader pluginClassLoader = new URLClassLoader(
                pluginUrls.toArray(URL[]::new),
                originalClassLoader
        )) {
            withPluginClassLoader(pluginClassLoader, () -> {
                // 此时 ShiroProperties 已由 Spring 正常初始化完成，@ConfigurationProperties 已绑定
                ShiroProperties shiroProperties = applicationContext.getBean(ShiroProperties.class);
                registerBotPlugins(shiroProperties, pluginClassLoader);
                printPluginSummary();
            });
        } catch (IOException e) {
            log.error("Plugin BotPlugin registration failed", e);
        }
    }

    // ==================== 插件扫描与注册 ====================
    /**
     * 扫描插件 jar 内所有 Spring 注解类，注册为 BeanDefinition。
     */
    private void scanAndRegister(BeanDefinitionRegistry registry, ClassLoader pluginClassLoader) throws Exception {
        File pluginDir = new File(pluginScanPath);
        File[] jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null) {
            return;
        }

        int count = 0;
        for (File jar : jars) {
            try (JarFile jarFile = new JarFile(jar)) {
                for (JarEntry entry : jarFile.stream().toList()) {
                    if (!entry.getName().endsWith(".class") || entry.isDirectory()) {
                        continue;
                    }
                    String className = entry.getName().replace("/", ".").replace(".class", "");
                    Class<?> clazz = loadPluginBeanClass(className, pluginClassLoader);
                    if (clazz != null) {
                        registerBeanDefinition(clazz, registry);
                        count++;
                    }
                }
            }
        }
        log.info("Plugin BeanDefinition registration completed, {} total", count);
    }

    /**
     * 尝试从插件 ClassLoader 加载一个 Spring bean 类。 若非 bean、由父加载器加载或加载失败，则返回 null
     * 并记录相应日志。
     */
    private Class<?> loadPluginBeanClass(String className, ClassLoader pluginClassLoader) {
        try {
            Class<?> clazz = Class.forName(className, false, pluginClassLoader);
            if (!isBean(clazz)) {
                return null;
            }
            // 检查类是否由插件 ClassLoader 加载，若由父加载器加载说明主程序已存在同名类
            if (clazz.getClassLoader() != pluginClassLoader) {
                // 插件 JAR 中的类被主程序已加载的同名类覆盖(ClassLoader 为父加载器)，JAR 中的版本将被忽略。
                // 请检查主程序是否已存在包名类名完全相同的类，考虑重命名插件中的类或移除主程序中的重复类。
                log.warn("Class {} from plugin JAR is shadowed by a same-named class from the main "
                        + "application. The JAR version will be ignored. "
                        + "Rename the plugin class or remove the duplicate from the main app.",
                        className);
                return null;
            }
            return clazz;
        } catch (ClassNotFoundException | LinkageError e) {
            log.debug("Skipping class: {}", className, e);
            return null;
        }
    }

    /**
     * 使用 ServiceLoader 发现并注册 BotPlugin 实现。 必须在 ApplicationContext 完全刷新后调用，因为需要
     * ShiroProperties 已完成 YAML 绑定。
     */
    private void registerBotPlugins(ShiroProperties shiroProperties, ClassLoader pluginClassLoader) {
        ServiceLoader<BotPlugin> loader = ServiceLoader.load(BotPlugin.class, pluginClassLoader);
        int count = 0;

        DefaultListableBeanFactory beanFactory
                = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        for (BotPlugin plugin : loader) {
            Class<? extends BotPlugin> pluginClass = plugin.getClass().asSubclass(BotPlugin.class);

            // 检查 BotPlugin 实现类是否由插件 ClassLoader 加载
            if (pluginClass.getClassLoader() != pluginClassLoader) {
                // [SPI] BotPlugin 被主程序的类加载器加载(非插件 ClassLoader)，
                // 说明主程序中已存在包名类名完全相同的 BotPlugin 实现，JAR 中的版本将被跳过。
                // 请检查主程序是否重复定义了该类。
                log.warn("[SPI] BotPlugin {} was loaded by the main ClassLoader instead of the plugin "
                        + "ClassLoader — a same-named implementation likely exists in the main app. "
                        + "The JAR version will be skipped.",
                        pluginClass.getName());
                continue;
            }

            String beanName = generateBeanName(pluginClass);

            beanFactory.autowireBean(plugin);
            beanFactory.registerSingleton(beanName, plugin);

            shiroProperties.getPluginList().add(pluginClass);
            log.info("[SPI] Registered BotPlugin: {} (bean: {})", pluginClass.getName(), beanName);
            count++;
        }
        log.info("SPI successfully loaded {} BotPlugins", count);
    }

    private void printPluginSummary() {
        ShiroProperties shiroProperties = applicationContext.getBean(ShiroProperties.class);
        List<Class<? extends BotPlugin>> allPlugins = shiroProperties.getPluginList();
        if (allPlugins.isEmpty()) {
            // 当前没有任何 BotPlugin 被注册（YAML 和 SPI 均为空），请检查配置
            log.warn("No BotPlugin registered — both YAML and SPI are empty. Check your configuration.");
        } else {
            log.info("BotPlugin summary ({} total): {}", allPlugins.size(),
                    allPlugins.stream().map(Class::getName).collect(Collectors.joining(", ")));
        }
    }

    // ==================== JAR 收集与依赖解析 ====================
    /**
     * 收集所有 jar + 依赖，过滤主应用已有的框架级 jar。
     */
    private List<URL> collectAllPluginUrls() throws Exception {
        LinkedHashMap<String, URL> urlMap = new LinkedHashMap<>();
        File[] jars = findPluginJars();
        if (jars == null) {
            return List.of();
        }

        for (File jar : jars) {
            log.info("Loading plugin: {}", jar.getName());
            DependencyStats stats = collectDependencyUrls(jar, urlMap);
            log.info("Plugin {} dependency filter: {} kept, {} skipped",
                    jar.getName(), stats.kept(), stats.skipped());
            putIfAbsent(urlMap, jar);
        }

        log.info("Plugin ClassLoader contains {} jars in total", urlMap.size());
        return new ArrayList<>(urlMap.values());
    }

    private File[] findPluginJars() {
        File pluginDir = new File(pluginScanPath);
        if (!pluginDir.exists()) {
            return new File[0];
        }
        File[] jars = pluginDir.listFiles((d, n) -> n.endsWith(".jar"));
        return jars != null ? jars : new File[0];
    }

    private DependencyStats collectDependencyUrls(File jar, Map<String, URL> urlMap) throws IOException {
        int kept = 0;
        int skipped = 0;

        for (String dep : parseDependencies(jar)) {
            List<File> files = dependencyResolver.resolveDependency(dep);
            if (files.isEmpty()) {
                skipped++;
                continue;
            }
            for (File file : files) {
                if (putIfAbsent(urlMap, file)) {
                    kept++;
                } else {
                    skipped++;
                }
            }
        }
        return new DependencyStats(kept, skipped);
    }

    private boolean putIfAbsent(Map<String, URL> urlMap, File file) throws IOException {
        String key = file.getName();
        URL previous = urlMap.putIfAbsent(key, file.toURI().toURL());
        return previous == null;
    }

    private Set<String> parseDependencies(File jarFile) throws IOException {
        Set<String> dependencies = new HashSet<>();
        try (JarFile jar = new JarFile(jarFile)) {
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                String deps = manifest.getMainAttributes().getValue("Dependencies");
                if (deps != null) {
                    dependencies.addAll(Arrays.asList(deps.split(",\\s*")));
                }
            }
        }
        return dependencies;
    }

    // ==================== ClassLoader 切换工具 ====================
    /**
     * 在插件 ClassLoader 上下文中执行操作，执行完毕后自动恢复原始 ClassLoader。
     */
    private void withPluginClassLoader(ClassLoader pluginClassLoader, Runnable action) {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(pluginClassLoader);
            action.run();
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    // ==================== 静态工具方法 ====================
    private static boolean isBean(Class<?> clazz) {
        if (clazz.isInterface() || clazz.isEnum() || clazz.isAnnotation()
                || Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        return clazz.isAnnotationPresent(Component.class)
                || clazz.isAnnotationPresent(Service.class)
                || clazz.isAnnotationPresent(Repository.class)
                || clazz.isAnnotationPresent(Controller.class)
                || clazz.isAnnotationPresent(RestController.class);
    }

    private static void registerBeanDefinition(Class<?> clazz, BeanDefinitionRegistry registry) {
        String beanName = generateBeanName(clazz);
        if (registry.containsBeanDefinition(beanName)) {
            String existingClassName = resolveExistingClassName(registry.getBeanDefinition(beanName));
            // Bean 名冲突: 插件中的类 (bean) 无法注册，因为同名的 bean 已被注册。
            // 这通常是由于主程序中已存在包名类名完全相同的类，或不同包下存在同名的 @Component 类。
            log.warn("Bean name conflict: {} (bean: {}) from plugin cannot be registered — "
                    + "already registered by {}. "
                    + "Typically caused by a same-named class in the main app or across packages.",
                    clazz.getName(), beanName, existingClassName);
            return;
        }
        GenericBeanDefinition bd = new GenericBeanDefinition();
        // 直接设置已解析的 Class 对象而非类名字符串，避免后续 Spring 用 beanFactory 的
        // ClassLoader（已恢复为 originalClassLoader）去解析插件 JAR 内的类时找不到
        bd.setBeanClass(clazz);
        bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        bd.setLazyInit(false);
        registry.registerBeanDefinition(beanName, bd);
        log.debug("Successfully registered bean: {}", beanName);
    }

    /**
     * 从已有的 BeanDefinition 中解析出类名，用于冲突诊断日志。
     */
    private static String resolveExistingClassName(BeanDefinition existing) {
        String existingClassName = existing.getBeanClassName();
        if (existingClassName == null && existing instanceof AbstractBeanDefinition abd && abd.hasBeanClass()) {
            existingClassName = abd.getBeanClass().getName();
        }
        return existingClassName != null ? existingClassName : "unknown";
    }

    private static String generateBeanName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    private record DependencyStats(int kept, int skipped) {
    }
}
