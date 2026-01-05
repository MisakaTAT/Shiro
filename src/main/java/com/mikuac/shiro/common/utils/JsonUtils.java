package com.mikuac.shiro.common.utils;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@SuppressWarnings("unused")
public class JsonUtils {

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = createConfiguredObjectMapper();
    private static final AtomicReference<ObjectMapper> customObjectMapper = new AtomicReference<>();

    private JsonUtils() {
    }

    private static ObjectMapper createConfiguredObjectMapper() {
        return JsonMapper.builder()
                // 忽略 JSON 中存在但 Java 对象不存在的字段
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 序列化时不因空 Bean 报错
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }

    public static void setCustomObjectMapper(ObjectMapper mapper) {
        customObjectMapper.set(mapper);
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = customObjectMapper.get();
        return mapper != null ? mapper : DEFAULT_OBJECT_MAPPER;
    }

    public static Optional<JsonNode> parseObject(String jsonString) {
        try {
            return Optional.ofNullable(getObjectMapper().readTree(jsonString));
        } catch (JacksonException e) {
            log.error("Failed to parse JSON string", e);
            return Optional.empty();
        }
    }

    public static <T> List<T> parseArray(JsonNode json, Class<T> clazz) {
        try {
            ObjectMapper mapper = getObjectMapper();
            return mapper.convertValue(json,
                    mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IllegalArgumentException e) {
            log.error("Failed to parse JSON array", e);
            return Collections.emptyList();
        }
    }

    public static String toJSONString(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JacksonException e) {
            log.error("Failed to convert object to JSON string", e);
            return null;
        }
    }

    public static boolean isValid(String jsonString) {
        if (jsonString == null) return false;
        try {
            getObjectMapper().readTree(jsonString);
            return true;
        } catch (JacksonException e) {
            return false;
        }
    }

    public static <T> T readValue(String jsonString, TypeReference<T> typeReference) {
        try {
            return getObjectMapper().readValue(jsonString, typeReference);
        } catch (Exception e) {
            log.error("Failed to read value with TypeReference", e);
            return null;
        }
    }

    public static String nodeToString(JsonNode node) {
        if (node == null) {
            return "";
        }
        if (node.isString()) {
            return node.asString();
        }
        try {
            return getObjectMapper().writeValueAsString(node);
        } catch (JacksonException e) {
            return node.toString();
        }
    }

    public static long nodeToLong(JsonNode node) {
        if (node == null) {
            return 0;
        }
        if (node.isNumber()) {
            return node.asLong();
        }
        if (node.isString()) {
            try {
                return Long.parseLong(node.asString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public static JsonNode parseToJsonNode(Object value) {
        ObjectMapper mapper = getObjectMapper();
        if (value instanceof String s) {
            return tryParseJsonString(mapper, s);
        }
        return mapper.valueToTree(value);
    }

    private static JsonNode tryParseJsonString(ObjectMapper mapper, String s) {
        try (JsonParser parser = mapper.createParser(s)) {
            JsonToken first = parser.nextToken();
            // 仅当首个 token 是对象或数组时解析
            if (first == JsonToken.START_OBJECT || first == JsonToken.START_ARRAY) {
                return mapper.readTree(parser);
            }
            // 其他情况全部当作普通文本
            return mapper.getNodeFactory().stringNode(s);
        } catch (Exception e) {
            // 解析失败也当作文本
            return mapper.getNodeFactory().stringNode(s);
        }
    }

}