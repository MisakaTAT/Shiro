package com.mikuac.shiro.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@SuppressWarnings("unused")
public class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = createConfiguredObjectMapper();

    private static final AtomicReference<ObjectMapper> customObjectMapper = new AtomicReference<>();

    private static ObjectMapper createConfiguredObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 忽略 JSON 中存在但 Java 对象不存在的字段，避免因字段变动导致反序列化失败
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时不因空 Bean 报错
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    public static void setCustomObjectMapper(@Nullable ObjectMapper mapper) {
        customObjectMapper.set(mapper);
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = customObjectMapper.get();
        return mapper != null ? mapper : DEFAULT_OBJECT_MAPPER;
    }

    public static Optional<JsonNode> parseObject(String jsonString) {
        try {
            return Optional.ofNullable(getObjectMapper().readTree(jsonString));
        } catch (JsonProcessingException e) {
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

    @Nullable
    public static String toJSONString(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON string", e);
            return null;
        }
    }

    public static boolean isValid(String jsonString) {
        if (jsonString == null) return false;
        try {
            getObjectMapper().readTree(jsonString);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    @Nullable
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
        if (node.isTextual()) {
            return node.textValue();
        }
        try {
            return getObjectMapper().writeValueAsString(node);
        } catch (JsonProcessingException e) {
            return node.toString();
        }
    }

    public static JsonNode parseToJsonNode(Object value) {
        ObjectMapper mapper = getObjectMapper();
        if (value instanceof String s) {
            JsonNode node = tryParseJsonString(mapper, s);
            return node != null ? node : mapper.getNodeFactory().textNode(s);
        }
        return mapper.valueToTree(value);
    }

    private static JsonNode tryParseJsonString(ObjectMapper mapper, String s) {
        try (JsonParser parser = mapper.createParser(s)) {
            JsonToken first = parser.nextToken();
            if (first == null) return null;
            JsonNode node = mapper.readTree(parser);
            if (parser.nextToken() == null) {
                return node;
            }
        } catch (IOException ignored) {
            // ignored
        }
        return null;
    }

}