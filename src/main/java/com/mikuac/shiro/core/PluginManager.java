package com.mikuac.shiro.core;

import com.mikuac.shiro.properties.ShiroProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PluginManager {

    private final ShiroProperties shiroProperties;
    private final ApplicationContext applicationContext;
    private final DependencyResolver dependencyResolver;

    @Getter
    private URLClassLoader pluginClassLoader;

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

    private void loadPlugins() throws IOException {
        File pluginDir = getPluginDirectory();
        if (pluginDir == null) {
            return;
        }

        List<URL> pluginUrls = new ArrayList<>();
        for (File jar : Objects.requireNonNull(pluginDir.listFiles((dir, name) -> name.endsWith(".jar")))) {
            // 解析插件依赖
            Set<String> dependencies = parseDependencies(jar);
            log.info("Parsing plugin: {}", jar.getAbsolutePath());
            resolveDependencies(dependencies);
            pluginUrls.add(jar.toURI().toURL());
        }

        if (pluginUrls.isEmpty()) {
            return;
        }

        // 添加依赖库路径
        pluginUrls.addAll(scanDependencyJars());
        this.pluginClassLoader = createPluginClassLoader(pluginUrls);
        registerPlugins(this.pluginClassLoader);
    }

    // 解析清单文件中的依赖
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

    // 解析依赖
    private void resolveDependencies(Set<String> dependencies) {
        dependencies.stream()
                .filter(this::isDependencyMissing)
                .forEach(dep -> {
                    try {
                        dependencyResolver.resolveDependency(dep);
                    } catch (ArtifactResolutionException e) {
                        log.error("Failed to resolve dependency: {}", dep, e);
                    }
                });
    }

    // 检查依赖是否已存在
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

    // 扫描依赖库目录
    private List<URL> scanDependencyJars() throws IOException {
        File depDir = DependencyResolver.DEPENDENCIES_DIR;
        List<URL> urls = new ArrayList<>();
        if (depDir.exists()) {
            try (Stream<Path> pathStream = Files.walk(depDir.toPath())) {
                pathStream
                        .filter(path -> path.toString().endsWith(".jar"))
                        .forEach(path -> {
                            try {
                                urls.add(path.toUri().toURL());
                                log.debug("Added dependency: {}", path.getFileName());
                            } catch (MalformedURLException e) {
                                log.error("Invalid dependency path: {}", path, e);
                            }
                        });
            }
        }
        return urls;
    }

    private File getPluginDirectory() {
        String pluginScanPath = shiroProperties.getPluginScanPath();
        File pluginDir = new File(pluginScanPath);

        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            log.info("Plugin directory does not exist or is not a directory: {}", pluginScanPath);
            return null;
        }

        return pluginDir;
    }

    private URLClassLoader createPluginClassLoader(List<URL> urls) {
        final ClassLoader parentLoader = Thread.currentThread().getContextClassLoader();

        // 定义禁止插件加载的包前缀
        final Set<String> forbiddenPackages = Set.of(
                "ch.qos.logback.",
                "org.slf4j.",
                "org.apache.logging."
        );

        return new URLClassLoader(urls.toArray(new URL[0]), parentLoader) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                // 禁止加载核心日志库
                for (String pkg : forbiddenPackages) {
                    if (name.startsWith(pkg)) {
                        return parentLoader.loadClass(name);
                    }
                }

                // 优先加载插件类
                try {
                    return findClass(name);
                } catch (ClassNotFoundException e) {
                    return super.loadClass(name);
                }
            }

            @Override
            public URL getResource(String name) {

                URL url;
                url = parentLoader.getResource(name);// 优先从父级加载
                if (url == null) url = findResource(name);// 父级没有从插件自身查找
                if (url == null) url = super.getResource(name);
                return url;
            }
        };
    }

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

    private String generateBeanName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

}