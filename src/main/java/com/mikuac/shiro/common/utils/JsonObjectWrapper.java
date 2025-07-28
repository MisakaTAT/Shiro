package com.mikuac.shiro.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonObjectWrapper {

    private final ObjectNode objectNode;
    private final ObjectMapper objectMapper;

    public JsonObjectWrapper() {
        this.objectMapper = JsonUtils.getObjectMapper();
        this.objectNode = objectMapper.createObjectNode();
    }

    public JsonObjectWrapper(ObjectNode objectNode) {
        this.objectMapper = JsonUtils.getObjectMapper();
        this.objectNode = objectNode;
    }

    public JsonObjectWrapper(String jsonString) {
        this.objectMapper = JsonUtils.getObjectMapper();
        JsonNode node = JsonUtils.parseObject(jsonString);
        if (node instanceof ObjectNode v) {
            this.objectNode = v;
        } else {
            this.objectNode = objectMapper.createObjectNode();
        }
    }

    public JsonObjectWrapper put(String key, Object obj) {
        if (obj == null) {
            objectNode.putNull(key);
        } else if (obj instanceof String value) {
            objectNode.put(key, value);
        } else if (obj instanceof Integer value) {
            objectNode.put(key, value);
        } else if (obj instanceof Long value) {
            objectNode.put(key, value);
        } else if (obj instanceof Boolean value) {
            objectNode.put(key, value);
        } else if (obj instanceof Double value) {
            objectNode.put(key, value);
        } else if (obj instanceof Float value) {
            objectNode.put(key, value);
        } else {
            // valueToTree 性能开销会更大一些，所以尽量先判断类型
            objectNode.set(key, objectMapper.valueToTree(obj));
        }
        return this;
    }

    public Object get(String key) {
        JsonNode node = objectNode.get(key);
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isInt()) {
            return node.asInt();
        }
        if (node.isLong()) {
            return node.asLong();
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isDouble()) {
            return node.asDouble();
        }
        return node;
    }

    public String getString(String key) {
        JsonNode node = objectNode.get(key);
        return node != null ? node.asText() : null;
    }

    public boolean containsKey(String key) {
        return objectNode.has(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        Object value = get(key);
        return value != null ? value : defaultValue;
    }

    public void clear() {
        objectNode.removeAll();
    }

    public <T> T to(Class<T> clazz) {
        try {
            return objectMapper.treeToValue(objectNode, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public String toJSONString() {
        try {
            return objectMapper.writeValueAsString(objectNode);
        } catch (Exception e) {
            return JsonUtils.toJSONString(objectNode);
        }
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    public static JsonObjectWrapper parseObject(String jsonString) {
        return new JsonObjectWrapper(jsonString);
    }

}