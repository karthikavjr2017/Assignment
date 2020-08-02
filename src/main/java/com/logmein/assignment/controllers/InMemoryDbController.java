package com.logmein.assignment.controllers;

import com.logmein.assignment.services.InMemoryDBService;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/")
public class InMemoryDbController implements InMemoryDBService {

    private ReentrantLock reentrantLock;
    private HashMap<String, String> database;
    private HashMap<String, HashMap<String, String>> transactions;

    public InMemoryDbController() {
        this.reentrantLock = new ReentrantLock(true);
        transactions = new HashMap<>();
        database = new HashMap<>();
    }

    private final long timeToWaitForALock=200L;

    /**
     *
     * @return health check of the application
     */
    @GetMapping(value = "/ping")
    @ResponseBody
    public String ping(){
        return "Up and running";
    }


    @RequestMapping(value = "/put/{key}/{value}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void put(@PathVariable("key") String key, @PathVariable("value") String value) throws InterruptedException {
        Boolean lockAcquired = reentrantLock.tryLock(timeToWaitForALock, TimeUnit.MILLISECONDS);
        if (lockAcquired) {
            System.out.println("Entering key" + " "+key +" "+"and value"+" "+value);
            try {
                database.put(key, value);
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    @RequestMapping(value = "/put/{key}/{value}/{transactionId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void put(@PathVariable("key") String key, @PathVariable("value") String value, @PathVariable("transactionId") String transactionId) throws Exception {

        checkTransaction(transactionId);

        HashMap<String, String> map = transactions.get(transactionId);
        if (map.containsKey(key)) {
            throw new Exception(String.format("Key '%s' already exists in transactionId '%s'", transactionId, key));
        }

        Boolean lockAcquired = reentrantLock.tryLock(timeToWaitForALock, TimeUnit.MILLISECONDS);
        if (lockAcquired) {

            try {
                map.put(key, value);
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    //check if the transaction is valid or not
    private void checkTransaction(String transactionId) throws Exception {
        if (transactionId == null || transactionId == "")
            throw new IllegalArgumentException("Invalid transactionId");

        if (!transactions.containsKey(transactionId))
            throw new IllegalArgumentException(String.format("Transaction ID '%s' already exists", transactionId));

    }

    @GetMapping(value = "/get/{key}")
    @ResponseBody
    public String get(@PathVariable("key") String key){
        System.out.println("Entering key" + " "+key);
        Boolean ifKeyPresent = database.containsKey(key);
        if(ifKeyPresent)
        return database.get(key);
        else
            return "null";
    }

    @RequestMapping(value = "/get/{key}/{transactionId}")
    @ResponseBody
    public String get(@PathVariable("key") String key, @PathVariable("transactionId") String transactionId) throws Exception {
        checkTransaction(transactionId);

        HashMap<String, String> map = transactions.get(transactionId);

        if (map.containsKey(key)) {
            return map.get(key);
        } else if (database.containsKey(key)) {
            return database.get(key);
        }
        return "null";
    }

    @RequestMapping(value = "/delete/{key}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void delete(@PathVariable("key") String key) throws InterruptedException {
        if (database.containsKey(key)) {
            Boolean lockAcquired = reentrantLock.tryLock(timeToWaitForALock, TimeUnit.MILLISECONDS);
            if (lockAcquired) {

                try {
                    database.remove(key);
                } finally {
                    reentrantLock.unlock();
                }
            }
        }
    }

    @RequestMapping(value = "/delete/{key}/{transactionId}")
    @ResponseBody
    public void delete(@PathVariable("key") String key, @PathVariable("transactionId") String transactionId) throws Exception {

        checkTransaction(transactionId);

        HashMap<String, String> entry = transactions.get(transactionId);

        if (!entry.containsKey(key)) {
            throw new Exception(String.format("Invalid key '%s'", key));
        }

        Boolean lockAcquired = reentrantLock.tryLock(timeToWaitForALock, TimeUnit.MILLISECONDS);
        if (lockAcquired) {
            try {
                entry.remove(key);
            } finally {
                reentrantLock.unlock();
            }
        }

    }

    @RequestMapping(value = "/createTransaction/{transactionId}")
    @ResponseBody
    public void createTransaction(@PathVariable("transactionId") String transactionId) throws Exception {

        if (transactions.containsKey(transactionId)) {
            throw new Exception("Transaction Id '%s' is already active");
        }

        Boolean lockAcquired = reentrantLock.tryLock(timeToWaitForALock, TimeUnit.MILLISECONDS);
        if (lockAcquired) {
            try {
                transactions.put(transactionId, new HashMap<>());
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    @RequestMapping(value = "/rollbackTransaction/{transactionId}")
    @ResponseBody
    public void rollbackTransaction(@PathVariable("transactionId") String transactionId) throws Exception {
        checkTransaction(transactionId);

        Boolean lockAcquired = reentrantLock.tryLock(timeToWaitForALock, TimeUnit.MILLISECONDS);

        if (lockAcquired) {
            try {
                transactions.remove(transactionId);
            } finally {
                reentrantLock.unlock();
            }
        }

    }

    @RequestMapping(value = "/commitTransaction/{transactionId}")
    @ResponseBody
    public void commitTransaction(@PathVariable("transactionId") String transactionId) throws Exception {
        checkTransaction(transactionId);

        HashMap<String, String> map = transactions.get(transactionId);

        Boolean lockAcquired = reentrantLock.tryLock(timeToWaitForALock, TimeUnit.MILLISECONDS);
        if (lockAcquired) {
            try {
                for (String key : map.keySet()) {
                    if (database.containsKey(key)) {
                        transactions.remove(transactionId);
                        throw new Exception(String.format("Key '%s' already exists in the database", key));
                    }
                }

                for (String key : map.keySet()) {
                    database.put(key, map.get(key));
                }

                transactions.remove(transactionId);

            } catch (Exception ex){
            }

            finally {
                reentrantLock.unlock();
            }
        }
    }
}
