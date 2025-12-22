package com.mikuac.shiro.common.utils;

import tools.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.node.StringNode;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Test
    void testParseToJsonNode_withValidJsonString() {
        String json = "{\"name\":\"Alice\",\"age\":30}";
        JsonNode node = JsonUtils.parseToJsonNode(json);
        assertTrue(node.isObject());
        assertEquals("Alice", node.get("name").asString());
        assertEquals(30, node.get("age").asInt());
    }

    @Test
    void testParseToJsonNode_withInvalidJsonString() {
        String notJson = "Just a plain string";
        JsonNode node = JsonUtils.parseToJsonNode(notJson);
        assertInstanceOf(StringNode.class, node);
        assertEquals(notJson, node.asString());
    }

    @Test
    void testParseToJsonNode_withEmptyString() {
        String empty = "";
        JsonNode node = JsonUtils.parseToJsonNode(empty);
        assertInstanceOf(StringNode.class, node);
        assertEquals(empty, node.asString());
    }

    @Test
    void testParseToJsonNode_withNonStringObject() {
        Person person = new Person("Bob", 25);
        JsonNode node = JsonUtils.parseToJsonNode(person);
        assertTrue(node.isObject());
        assertEquals("Bob", node.get("name").asString());
        assertEquals(25, node.get("age").asInt());
    }

    static class Person {
        public String name;
        public int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

}
