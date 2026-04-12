package com.mikuac.shiro.plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DependencyConflictResolver {

    private final Map<String, ResolvedArtifact> selected = new HashMap<>();

    /**
     * 尝试加入依赖
     */
    public void add(String groupId, String artifactId, File file, int depth) {

        String key = groupId + ":" + artifactId;

        ResolvedArtifact existing = selected.get(key);

        if (existing == null) {
            selected.put(key, new ResolvedArtifact(file, depth));
            return;
        }

        // 🔥 nearest-wins：层级更小的优先
        if (depth < existing.getDepth()) {
            selected.put(key, new ResolvedArtifact(file, depth));
        }
    }

    /**
     * 获取最终结果
     */
    public Collection<File> result() {
        return selected.values().stream()
                .map(ResolvedArtifact::getFile)
                .toList();
    }
}