package com.mikuac.shiro.common.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadSafeMultiValueMap<K, V> {
    private final Map<K, List<V>> map = new ConcurrentHashMap<>();

    public void add(K key, V value) {
        map.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(value);
    }

    public List<V> get(K key) {
        List<V> list = map.get(key);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    public Map<K, List<V>> asMap() {
        Map<K, List<V>> result = new HashMap<>();
        for (Map.Entry<K, List<V>> entry : map.entrySet()) {
            synchronized (entry.getValue()) {
                result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        return Collections.unmodifiableMap(result);
    }

    public int size() {
        return map.size();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public void clear() {
        map.clear();
    }
}
