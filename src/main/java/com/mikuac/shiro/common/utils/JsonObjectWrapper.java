package com.mikuac.shiro.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mikuac.shiro.exception.ShiroException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public class JsonObjectWrapper {

    private final ObjectNode objectNode;
    private static final ObjectMapper OBJECT_MAPPER = JsonUtils.getObjectMapper();

    private static final Map<Class<?>, ValueSetter> VALUE_SETTERS = new HashMap<>();

    static {
        VALUE_SETTERS.put(String.class, (node, key, value) -> node.put(key, (String) value));
        VALUE_SETTERS.put(Integer.class, (node, key, value) -> node.put(key, (Integer) value));
        VALUE_SETTERS.put(Long.class, (node, key, value) -> node.put(key, (Long) value));
        VALUE_SETTERS.put(Boolean.class, (node, key, value) -> node.put(key, (Boolean) value));
        VALUE_SETTERS.put(Double.class, (node, key, value) -> node.put(key, (Double) value));
        VALUE_SETTERS.put(Float.class, (node, key, value) -> node.put(key, (Float) value));
    }

    @FunctionalInterface
    interface ValueSetter {
        void set(ObjectNode node, String key, Object value);
    }

    public JsonObjectWrapper() {
        this.objectNode = OBJECT_MAPPER.createObjectNode();
    }

    public JsonObjectWrapper(ObjectNode objectNode) {
        this.objectNode = objectNode != null ? objectNode : OBJECT_MAPPER.createObjectNode();
    }

    public JsonObjectWrapper(String jsonString) {
        this.objectNode = JsonUtils.parseObject(jsonString)
                .filter(ObjectNode.class::isInstance)
                .map(ObjectNode.class::cast)
                .orElseGet(OBJECT_MAPPER::createObjectNode);
    }

    public JsonObjectWrapper put(String key, Object value) {
        if (value == null) {
            objectNode.putNull(key);
            return this;
        }

        Class<?> clazz = value.getClass();
        ValueSetter setter = VALUE_SETTERS.get(clazz);
        if (setter != null) {
            setter.set(objectNode, key, value);
        } else {
            objectNode.set(key, OBJECT_MAPPER.valueToTree(value)); // 性能开销略大，仅在非基础类型时使用
        }
        return this;
    }

    public Object get(String key) {
        JsonNode node = objectNode.get(key);
        if (node == null || node.isNull()) return null;
        return switch (node.getNodeType()) {
            case STRING -> node.asText();
            case NUMBER -> {
                if (node.isInt()) {
                    yield node.asInt();
                } else {
                    if (node.isLong()) yield node.asLong();
                    yield node.asDouble();
                }
            }
            case BOOLEAN -> node.asBoolean();
            default -> node;
        };
    }

    public String getString(String key) {
        JsonNode node = objectNode.get(key);
        return (node != null && node.isTextual()) ? node.asText() : null;
    }

    public Integer getInt(String key) {
        JsonNode node = objectNode.get(key);
        return (node != null && node.isInt()) ? node.asInt() : null;
    }

    public Long getLong(String key) {
        JsonNode node = objectNode.get(key);
        return (node != null && node.isLong()) ? node.asLong() : null;
    }

    public Boolean getBoolean(String key) {
        JsonNode node = objectNode.get(key);
        return (node != null && node.isBoolean()) ? node.asBoolean() : null;
    }

    public boolean containsKey(String key) {
        return objectNode.has(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }

    public JsonObjectWrapper remove(String key) {
        objectNode.remove(key);
        return this;
    }

    public void clear() {
        objectNode.removeAll();
    }

    public int size() {
        return objectNode.size();
    }

    public Set<String> keySet() {
        Iterable<String> iterable = objectNode::fieldNames;
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toSet());
    }

    public <T> T to(Class<T> clazz) {
        try {
            return OBJECT_MAPPER.treeToValue(objectNode, clazz);
        } catch (Exception e) {
            throw new ShiroException("Cannot convert to class: " + clazz.getName(), e);
        }
    }

    public String toJSONString() {
        try {
            return OBJECT_MAPPER.writeValueAsString(objectNode);
        } catch (Exception e) {
            throw new ShiroException("JSON serialization failed", e);
        }
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    public static JsonObjectWrapper parseObject(String jsonString) {
        return new JsonObjectWrapper(jsonString);
    }

    public ObjectNode raw() {
        return objectNode;
    }

}
