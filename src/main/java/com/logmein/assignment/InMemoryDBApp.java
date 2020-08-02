package com.logmein.assignment;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.logmein.assignment"})
public class InMemoryDBApp {

    public static void main(String[] args) {
        final Logger LOGGER = Logger.getLogger(InMemoryDBApp.class);
        SpringApplication.run(InMemoryDBApp.class, args);

    }
}
