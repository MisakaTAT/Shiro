package com.mikuac.shiro.core;

import com.mikuac.shiro.core.plugin_loader.DependencyResolver;
import com.mikuac.shiro.properties.ShiroProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 插件管理器
 *
 * @author Zero
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PluginManager {

    private final ShiroProperties shiroProperties;
    private final ApplicationContext applicationContext;

    /**
     * 插件类加载器
     */
    @Getter
    private URLClassLoader pluginClassLoader;

    /**
     * 初始化插件
     */
    @PostConstruct
    public void initPlugins() {
        try {
            loadPlugins();
        } catch (IOException e) {
            log.error("Plugin loading failed due to I/O error", e);
        } catch (Exception e) {
            log.error("Plugin loading failed", e);
        }
    }

    /**
     * 应用关闭时清理资源
     */
    @PreDestroy
    public void cleanup() {
        if (pluginClassLoader != null) {
            try {
                pluginClassLoader.close();
                log.debug("Plugin class loader closed successfully");
            } catch (IOException e) {
                log.warn("Failed to close plugin class loader", e);
            }
        }
    }

    /**
     * 加载插件
     */
    private void loadPlugins() throws IOException {
        File pluginDir = getPluginDirectory();
        if (pluginDir == null) {
            return;
        }

        List<URL> pluginUrls = new ArrayList<>();
        for (File jar : Objects.requireNonNull(pluginDir.listFiles((dir, name) -> name.endsWith(".jar")))) {
            // 新增：解析插件依赖
            Set<String> dependencies = parseDependencies(jar);
            log.info("parsing: {}", jar.getAbsolutePath());
            resolveDependencies(dependencies);

            pluginUrls.add(jar.toURI().toURL());
        }

        if (pluginUrls.isEmpty()) {
            return;
        }

        // 新增：添加依赖库路径
        pluginUrls.addAll(scanDependencyJars());
        this.pluginClassLoader = createPluginClassLoader(pluginUrls);
        registerPlugins(this.pluginClassLoader);
    }

    // 新增方法：解析清单文件中的依赖
    private Set<String> parseDependencies(File jarFile) throws IOException {
        Set<String> dependencies = new HashSet<>();
        try (JarFile jar = new JarFile(jarFile)) {
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                String deps = manifest.getMainAttributes().getValue("Dependencies");
                if (deps != null) {
                    Arrays.stream(deps.split(",\\s*"))
                            // .map(s -> s.split(":")[0] + ":" + s.split(":")[1]) // 提取 groupId:artifactId
                            .forEach(dependencies::add);
                }
            }
        }
        return dependencies;
    }

    DependencyResolver resolver = new DependencyResolver();

    // 新增方法：解析依赖
    private void resolveDependencies(Set<String> dependencies) {
        dependencies.stream()
                .filter(this::isDependencyMissing)
                .forEach(dep -> {
                    try {
                        log.info("resolving: {}", dep);
                        resolver.resolveDependency(dep);
                    } catch (ArtifactResolutionException e) {
                        log.error("resolve dependency failed: {}", dep, e);
                    }
                });
    }

    // 新增方法：检查依赖是否已存在
    private boolean isDependencyMissing(String groupArtifact) {
        String[] parts = groupArtifact.split(":");
        try {
            // 尝试加载依赖中的典型类
            Class.forName(parts[0].replace('.', '/') + "/" + parts[1] + "/Application");
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }

    // 新增方法：扫描依赖库目录
    private List<URL> scanDependencyJars() throws MalformedURLException {
        File depDir = DependencyResolver.dependenciesDir;
        List<URL> urls = new ArrayList<>();
        if (depDir.exists()) {
            for (File jar : Objects.requireNonNull(depDir.listFiles((dir, name) -> name.endsWith(".jar")))) {
                urls.add(jar.toURI().toURL());
                log.debug("已添加依赖库: {}", jar.getName());
            }
        }
        return urls;
    }

    /**
     * 获取插件目录
     */
    private File getPluginDirectory() {
        String pluginScanPath = shiroProperties.getPluginScanPath();
        File pluginDir = new File(pluginScanPath);

        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            log.info("Plugin directory does not exist or is not a directory: {}", pluginScanPath);
            return null;
        }

        return pluginDir;
    }

    /**
     * 扫描插件JAR文件
     */
    private List<URL> scanPluginJars(File pluginDir) throws MalformedURLException {
        List<URL> urls = new ArrayList<>();

        File[] jarFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            log.info("No plugin jar files found");
            return urls;
        }

        for (File jar : jarFiles) {
            urls.add(jar.toURI().toURL());
            log.debug("Added plugin jar: {}", jar.getName());
        }

        return urls;
    }

    /**
     * 创建插件类加载器
     */
    private URLClassLoader createPluginClassLoader(List<URL> urls) {
        final ClassLoader parentLoader = applicationContext.getClassLoader();

        if (parentLoader == null) {
            log.warn("Parent ClassLoader is null, using system ClassLoader instead");
            return new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader());
        }

        return new URLClassLoader(urls.toArray(new URL[0]), parentLoader) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    return parentLoader.loadClass(name);
                } catch (ClassNotFoundException e) {
                    return super.loadClass(name);
                }
            }
        };
    }

    /**
     * 从类加载器注册插件
     */
    private void registerPlugins(URLClassLoader classLoader) {
        ServiceLoader<BotPlugin> loader = ServiceLoader.load(BotPlugin.class, classLoader);
        int count = 0;

        for (BotPlugin plugin : loader) {
            if (plugin.getClass().isAnnotationPresent(Component.class)) {
                registerPlugin(plugin);
                count++;
            }
        }

        log.info("Successfully loaded {} plugins", count);
    }

    /**
     * 将插件注册到Spring容器
     */
    private void registerPlugin(BotPlugin plugin) {
        Class<? extends BotPlugin> pluginClass = plugin.getClass().asSubclass(BotPlugin.class);
        String beanName = generateBeanName(pluginClass);

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
                .getAutowireCapableBeanFactory();
        beanFactory.registerSingleton(beanName, plugin);

        // 保持插件列表兼容性
        shiroProperties.getPluginList().add(pluginClass);
        log.debug("Registered plugin: {}", pluginClass.getName());
    }

    /**
     * 从类生成Bean名称
     */
    private String generateBeanName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
}