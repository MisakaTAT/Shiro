package com.mikuac.shiro.common.utils;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>ScanUtils class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
public class ScanUtils implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 扫描注解类
     *
     * @param packageName 包名
     * @return 注解集合
     */
    public Set<Class<?>> scanAnnotation(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    .concat(ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName))
                            .concat("/*.class"));
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(packageSearchPath);
            MetadataReader metadataReader;
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    metadataReader = new CachingMetadataReaderFactory(resourceLoader).getMetadataReader(resource);
                    // 当类型为注解时添加到集合
                    if (metadataReader.getClassMetadata().isAnnotation()) {
                        classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                    }
                }
            }
            return classes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

}
