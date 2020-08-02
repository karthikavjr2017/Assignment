package com.logmein.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.logmein.assignment"})
public class InMemoryDBApp {

    public static void main(String[] args) {

        SpringApplication.run(InMemoryDBApp.class, args);

    }
}
