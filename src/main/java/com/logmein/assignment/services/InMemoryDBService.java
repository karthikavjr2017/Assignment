package com.logmein.assignment.services;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * author: karthikakella
 * The interface for the key value store database
 */

@Service
public interface InMemoryDBService {
    void put(String key, String value) throws InterruptedException;

    void put(String key, String value, String transactionId) throws Exception;

    String get(String key);

    String get(String key, String transactionId) throws Exception;

    void delete(String key) throws InterruptedException;

    void delete(String key, String transactionId) throws Exception;

    void createTransaction(String transactionId) throws Exception;

    void rollbackTransaction(String transactionId) throws Exception;

    void commitTransaction(String transactionId) throws Exception;
}
