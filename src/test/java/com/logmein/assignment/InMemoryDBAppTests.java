package com.logmein.assignment;

import com.logmein.assignment.controllers.InMemoryDbController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class InMemoryDBAppTests {
    @Autowired
    private InMemoryDbController controller;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void put() {
        try {
            String key = "example";
            String value = "foo";
            controller.put(key, value);
            String result = controller.get(key);
            assertEquals(value, result);
        } catch (InterruptedException e) {
        } finally {
            controller = null;
        }
    }
     //tests to create transaction
    @Test
        public void createTransaction() {

            try {
                controller.createTransaction("abc");

                controller.put("a", "foo", "abc");

                assertEquals("foo", controller.get("a", "abc"));

                controller.createTransaction("xyz");

                controller.put("a", "bar", "xyz");

                assertEquals("bar", controller.get("a", "xyz"));

                controller.commitTransaction("xyz");

                assertEquals("bar", controller.get("a"));

            } catch (Exception e) {
            } finally {
                controller = null;
            }
        }
    }
