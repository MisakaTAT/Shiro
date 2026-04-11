package com.mikuac.shiro.core;

import com.mikuac.shiro.properties.ShiroProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;

@Slf4j
@Component
public class PluginManager implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ShiroProperties shiroProperties;
    private DependencyResolver dependencyResolver;

    private URLClassLoader pluginClassLoader;

    // ====================== 获取 Spring Bean ======================
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.shiroProperties = applicationContext.getBean(ShiroProperties.class);
        this.dependencyResolver = new DependencyResolver(shiroProperties);
    }

    // ====================== 核心入口 ======================
    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        try {
            List<URL> urls = collectAllPluginUrls();

            if (urls.isEmpty()) {
                log.warn("没有检测到插件");
                return;
            }

            // ✅ 1. 创建统一 ClassLoader（只此一个）
            this.pluginClassLoader = new URLClassLoader(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader()
            );

            // ✅ 2. 注入 Spring（核心！）
            if (registry instanceof DefaultListableBeanFactory beanFactory) {
                beanFactory.setBeanClassLoader(pluginClassLoader);
            }

            log.info("插件 ClassLoader 初始化完成: {}", pluginClassLoader);

            // ✅ 3. 扫描并注册
            scanAndRegister(registry);

        } catch (Exception e) {
            log.error("插件加载失败", e);
        }
    }

    // ====================== 收集所有 jar ======================
    private List<URL> collectAllPluginUrls() throws IOException {
        List<URL> urls = new ArrayList<>();

        File pluginDir = new File(shiroProperties.getPluginScanPath());
        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            log.warn("插件目录不存在: {}", pluginDir);
            return urls;
        }

        File[] jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            return urls;
        }

        for (File jar : jars) {
            log.info("加载插件: {}", jar.getName());

            // 解析依赖
            Set<String> deps = parseDependencies(jar);
            resolveDependencies(deps);

            urls.add(jar.toURI().toURL());
        }

        // 加入依赖目录
        urls.addAll(scanDependencyJars());

        return urls;
    }

    /**
     * <h2>扫描并注册插件</h2>
     * @param registry bean注册表
     * @throws Exception
     */
    private void scanAndRegister(BeanDefinitionRegistry registry) throws Exception {
        File pluginDir = new File(shiroProperties.getPluginScanPath());
        File[] jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));

        if (jars == null) return;

        int count = 0;

        for (File jar : jars) {
            try (JarFile jarFile = new JarFile(jar)) {
                for (JarEntry entry : jarFile.stream().toList()) {

                    if (!entry.getName().endsWith(".class") || entry.isDirectory()) continue;

                    String className = entry.getName()
                            .replace("/", ".")
                            .replace(".class", "");

                    try {

                        Class<?> clazz = Class.forName(className, false, pluginClassLoader);

                        if (isBean(clazz)) {
                            registerBeanDefinition(clazz, registry);
                            count++;

                            // 记录插件（兼容旧逻辑）
                            if (BotPlugin.class.isAssignableFrom(clazz)) {
                                shiroProperties.getPluginList().add((Class<? extends BotPlugin>) clazz);
                            }
                        }

                    } catch (Throwable e) {
                        log.debug("跳过类: {}", className);
                    }
                }
            }
        }

        log.info("插件加载完成，共注册 {} 个 Bean", count);
    }

    /**
     * <h2>判断是否是Spring bean</h2>
     */
    private static boolean isBean(Class<?> clazz) {
        if (clazz.isInterface() || clazz.isEnum() || clazz.isAnnotation()
                || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }

        return clazz.isAnnotationPresent(Component.class)
                || clazz.isAnnotationPresent(Service.class)
                || clazz.isAnnotationPresent(Repository.class)
                || clazz.isAnnotationPresent(Controller.class)
                || clazz.isAnnotationPresent(RestController.class);
    }

    /**
     * <h2>注册bean</h2>
     * @param clazz 要注册的class
     * @param registry bean注册表
     */
    private static void registerBeanDefinition(Class<?> clazz, BeanDefinitionRegistry registry) {
        String beanName = generateBeanName(clazz);

        if (registry.containsBeanDefinition(beanName)) {
            log.warn("Bean 已存在: {}", beanName);
            return;
        }

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClassName(clazz.getName());
        bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        bd.setLazyInit(false);

        registry.registerBeanDefinition(beanName, bd);

        log.debug("注册插件 Bean: {} -> {}", beanName, clazz.getName());
    }

    /**
     * <h2>解析依赖</h2>
     * <p>读取jar包内的manifest来解析依赖</p>
     * @param jarFile 插件jar
     * @return 依赖列表
     * @throws IOException
     */
    private Set<String> parseDependencies(File jarFile) throws IOException {
        Set<String> dependencies = new HashSet<>();

        try (JarFile jar = new JarFile(jarFile)) {
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                String deps = manifest.getMainAttributes().getValue("Dependencies");
                if (deps != null && !deps.isEmpty()) {
                    dependencies.addAll(Arrays.asList(deps.split(",\\s*")));
                }
            }
        }

        return dependencies;
    }

    private void resolveDependencies(Set<String> dependencies) {
        dependencies.stream()
                .filter(this::isDependencyMissing)
                .forEach(dep -> {
                    try {
                        dependencyResolver.resolveDependency(dep);
                        log.info("依赖解析成功: {}", dep);
                    } catch (ArtifactResolutionException e) {
                        log.error("依赖解析失败: {}", dep, e);
                    }
                });
    }

    private boolean isDependencyMissing(String groupArtifact) {
        try {
            String[] parts = groupArtifact.split(":", 3);
            Path jarPath = DependencyResolver.DEPENDENCIES_DIR.toPath()
                    .resolve(parts[0].replace('.', '/'))
                    .resolve(parts[1])
                    .resolve(parts[2])
                    .resolve(parts[1] + "-" + parts[2] + ".jar");

            return !jarPath.toFile().exists();
        } catch (Exception e) {
            return true;
        }
    }

    private List<URL> scanDependencyJars() throws IOException {
        List<URL> urls = new ArrayList<>();
        File depDir = DependencyResolver.DEPENDENCIES_DIR;

        if (!depDir.exists()) return urls;

        try (Stream<Path> stream = Files.walk(depDir.toPath())) {
            stream.filter(p -> p.toString().endsWith(".jar"))
                    .forEach(p -> {
                        try {
                            urls.add(p.toUri().toURL());
                        } catch (Exception ignored) {}
                    });
        }

        return urls;
    }

    public static String generateBeanName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}