package com.hyperleon.demo.lock.api;

/**
 * exception for distribute lock
 *
 * @author leon
 * @date 2021-05-02 19:18
 **/
public class LockException extends Exception {

    public LockException() {
    }

    public LockException(String message) {
        super(message);
    }
}
