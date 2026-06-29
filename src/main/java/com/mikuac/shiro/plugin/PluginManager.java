
package com.mikuac.shiro.plugin;

import com.mikuac.shiro.annotation.common.ShiroPlugin;
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
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@Slf4j
@Component
public class PluginManager implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ShiroProperties shiroProperties;
    private DependencyResolver dependencyResolver;

    private ClassLoader pluginClassLoader;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.shiroProperties = applicationContext.getBean(ShiroProperties.class);
        this.dependencyResolver = new DependencyResolver(shiroProperties);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        try {
            List<URL> urls = collectAllPluginUrls();

            if (urls.isEmpty()) {
                log.warn("没有检测到插件");
                return;
            }

            // ✅ 1. 创建 ClassLoader
            pluginClassLoader = new URLClassLoader(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader()
            );

            // ✅ 2. 注入 Spring
            if (registry instanceof DefaultListableBeanFactory beanFactory) {
                beanFactory.setBeanClassLoader(pluginClassLoader);
            }

            Thread.currentThread().setContextClassLoader(pluginClassLoader);

            log.info("插件 ClassLoader 初始化完成");

            scanPlugins(registry);

        } catch (Exception e) {
            log.error("插件加载失败", e);
        }
    }

    private void scanPlugins(BeanDefinitionRegistry registry) throws Exception {

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(ShiroPlugin.class));

        scanner.setResourceLoader(new PathMatchingResourcePatternResolver(pluginClassLoader));

        // 👉 扫描所有 class（不限包！）
        Set<BeanDefinition> candidates = scanner.findCandidateComponents("");

        for (BeanDefinition bd : candidates) {

            String className = bd.getBeanClassName();
            Class<?> pluginClass = Class.forName(className, false, pluginClassLoader);

            ShiroPlugin annotation = pluginClass.getAnnotation(ShiroPlugin.class);

            String[] basePackages = new String[]{
                    pluginClass.getPackage().getName()
            };// 默认用当前类包
            if(annotation != null) {
                String[] scanBasePackages = annotation.scanBasePackages();
                if (scanBasePackages.length != 0) {
                    basePackages=scanBasePackages;
                }
            }

            log.info("加载插件入口: {}", className);

            // ✅ 用 Spring Scanner 扫描这个插件
            ClassPathBeanDefinitionScanner springScanner =
                    new ClassPathBeanDefinitionScanner(registry, true);

            springScanner.setResourceLoader(
                    new PathMatchingResourcePatternResolver(pluginClassLoader)
            );

            int count = springScanner.scan(basePackages);

            if (BotPlugin.class.isAssignableFrom(pluginClass)) {
                shiroProperties.getPluginList().add((Class<? extends BotPlugin>) pluginClass);
            }
            log.info("插件 {} 注册 {} 个 Bean", className, count);
        }
    }

    /**
     * 收集所有 jar + 依赖
     */
    private List<URL> collectAllPluginUrls() throws Exception {
        Map<String, URL> urlMap = new LinkedHashMap<>();

        File pluginDir = new File(shiroProperties.getPluginScanPath());
        if (!pluginDir.exists()) return List.of();

        File[] jars = pluginDir.listFiles((d, n) -> n.endsWith(".jar"));
        if (jars == null) return List.of();
        for (File jar : jars) {
            log.info("加载插件: {}", jar.getName());
            Set<String> deps = parseDependencies(jar);
            for (String dep : deps) {
                List<File> files = dependencyResolver.resolveDependency(dep);

                for (File file : files) {
                    String key = file.getName(); //按文件名去重
                    urlMap.putIfAbsent(key, file.toURI().toURL());
                }
            }

            urlMap.putIfAbsent(jar.getName(), jar.toURI().toURL());
        }

        return new ArrayList<>(urlMap.values());
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

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) {}
}