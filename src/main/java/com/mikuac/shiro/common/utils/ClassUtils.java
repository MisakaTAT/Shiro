package com.mikuac.shiro.common.utils;

import lombok.val;
import lombok.var;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * @author Zero
 */
public class ClassUtils {

    /**
     * file type
     */
    private final static String FILE_TYPE = "file";

    /**
     * jar type
     */
    private final static String JAR_TYPE = "jar";

    /**
     * @param packageName 包名
     * @return 类名列表
     */
    public static List<String> getClassName(String packageName) {
        List<String> classNames = null;
        val classLoader = Thread.currentThread().getContextClassLoader();
        val packagePath = packageName.replace(".", "/");
        val url = classLoader.getResource(packagePath);
        if (url != null) {
            val type = url.getProtocol();
            if (FILE_TYPE.equals(type)) {
                classNames = getClassNamesByFile(url.getPath());
            }
            if (JAR_TYPE.equals(type)) {
                classNames = getClassNamesByJar(url.getPath());
            }
        }
        return classNames;
    }

    /**
     * @param filePath 文件路径
     * @return 类名列表
     */
    private static List<String> getClassNamesByFile(String filePath) {
        val classNames = new ArrayList<String>();
        val listFiles = new File(filePath).listFiles();
        if (listFiles != null) {
            for (val listFile : listFiles) {
                var listFilePath = listFile.getPath();
                if (listFilePath.endsWith(".class")) {
                    listFilePath = listFilePath.substring(listFilePath.indexOf("\\classes") + 37, listFilePath.lastIndexOf("."));
                    listFilePath = listFilePath.replace("\\", ".");
                    classNames.add(listFilePath);
                }
            }
        }
        return classNames;
    }

    /**
     * @param jarPath jar 文件路径
     * @return 类名列表
     */
    private static List<String> getClassNamesByJar(String jarPath) {
        val classNames = new ArrayList<String>();
        val jarInfo = jarPath.split("!");
        val jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        val packageName = jarInfo[1].substring(1);
        try {
            val jarFile = new JarFile(jarFilePath);
            val entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                val jarEntry = entries.nextElement();
                var entryName = jarEntry.getName();
                if (!entryName.startsWith(packageName)) {
                    continue;
                }
                if (!entryName.endsWith(".class")) {
                    continue;
                }
                entryName = entryName.replace("/", ".").substring(28, entryName.lastIndexOf("."));
                classNames.add(entryName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classNames;
    }

}
