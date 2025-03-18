package com.mikuac.shiro.core;

import com.mikuac.shiro.properties.ShiroProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

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

        List<URL> pluginUrls = scanPluginJars(pluginDir);
        if (pluginUrls.isEmpty()) {
            return;
        }

        this.pluginClassLoader = createPluginClassLoader(pluginUrls);
        registerPlugins(this.pluginClassLoader);
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

        DefaultListableBeanFactory beanFactory =
                (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
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