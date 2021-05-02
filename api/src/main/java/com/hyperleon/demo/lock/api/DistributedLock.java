package com.hyperleon.demo.lock.api;

/**
 * contract of distribute lock
 *
 * @author leon
 * @date 2021-05-02 19:14
 **/
public interface DistributedLock {

    /**
     * acquire lock
     *
     * @return client token,also the value for the given key.It is never null
     * @throws LockException exception occur during invoke
     */
    String acquire() throws LockException;

    /**
     * release lock
     *
     * @param key lock key
     * @param clientToken the result of acquire method
     * @see DistributedLock#acquire()
     * @return true for success release
     * @throws LockException exception occur during invoke
     */
    boolean release(String key,String clientToken) throws LockException;
}
