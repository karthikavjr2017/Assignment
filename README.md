# Assignment 

Implementing an In-memory database as a key value store:
Using HashMap to store the key and values. 

# Technology and Tools used

    Spring-boot App with rest Annotations
    Java-version: 1.8
    maven-version: 3.6.3 


# How to run

    mvn spring-boot:run
    
    The rest API will be available at:

    http://localhost:8080

# API Operations and usage

For request sequence without transactions:

    1. put(key,value)
    http://localhost:8080/put/key/value
    example: http://localhost:8080/put/example/foo
    
    2. get(key)
    http://localhost:8080/get/key
    example: http://localhost:8080/get/example
    
    3. put(key,value,transactionId)
    http://localhost:8080/put/key/value/transactionId
    example: http://localhost:8080/put/a/bar/xyz
    
    4. delete(key)
    http://localhost:8080/delete/key
    example: http://localhost:8080/delete/example
    
    5. delete(key,value,transactionId)
    http://localhost:8080/delete/key/value/transactionId
    example: http://localhost:8080/delete/example/abc
    
For request sequence with transactions:

    1. createTransaction(transactionId)
    http://localhost:8080/createTransaction/transactionId
    
    2. rollbackTransaction(transactionId)
    http://localhost:8080/rollbackTransaction/transactionId
    
    3. commitTransaction(transactionId)
    http://localhost:8080/commitTransaction/transactionId
    
# Implementation
    Since HashMap is not threadsafe, inorder to maintain the 
    transaction read committed isolation level, I am using ReentrantLock to prevent dirty_read operation.
    Alternatively, this can also be achieved by (@Transactional(isolation = Isolation.READ_COMMITTED)) in Spring.
    
    In Addition, the following have been implemented to maintain J2EE standards.
    
    1. Externalized Logging.
    2. Exception Handling.
    3. Unit tests for the Rest API operations.
    
# TimeComplexity

    The best and average cases for get, put and delete operations is O(1)
    and worst case is O(logn)
        