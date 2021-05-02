package com.hyperleon.demo.lock.redis;

import com.hyperleon.demo.lock.api.ClientTokenGenerate;
import com.hyperleon.demo.lock.api.DistributedLock;
import com.hyperleon.demo.lock.api.LockException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * distribute lock based redis
 *
 * @author leon
 * @date 2021-05-02 19:30
 **/
public class RedisLock implements DistributedLock, ClientTokenGenerate {

    /**
     * client for redis
     */
    private Jedis jedis;

    /**
     * acquire lock timeout limit
     */
    private int timeoutMillis;

    /**
     * lock expire time
     */
    private int expireMillis;

    /**
     * represent lock
     */
    private String lockKey;

    private static final String UNLOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    private static final Long UNLOCK_SUCCESS = 1L;

    /**
     * sync
     */
    private final ReentrantLock lock = new ReentrantLock();


    public RedisLock(String lockKey,Jedis jedis, int timeoutMillis, int expireMillis) {
        this.jedis = jedis;
        this.timeoutMillis = timeoutMillis;
        this.expireMillis = expireMillis;
        this.lockKey = lockKey;
    }


    @Override
    public String acquire() throws LockException {
        try {
            lock.lock();
            long acquireTimeout = System.currentTimeMillis() + timeoutMillis;
            String clientToken = generateToken();

            long begin = System.currentTimeMillis();
            while (System.currentTimeMillis() - begin < acquireTimeout) {
                SetParams setParams = new SetParams();
                setParams.nx().ex(expireMillis);
                String result = jedis.set(lockKey, clientToken, setParams);
                if (Objects.nonNull(result)) {
                    return clientToken;
                }
            }
            throw new LockException("time out");
        } catch (Exception e) {
            throw new LockException(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean release(String key,String clientToken) throws LockException {
        try {
            lock.lock();
            Object evalResult = jedis.eval(
                    UNLOCK_LUA_SCRIPT,
                    Collections.singletonList(key),
                    Collections.singletonList(clientToken));
            if (UNLOCK_SUCCESS.equals(evalResult)) {
                return true;
            }
            throw new LockException("release lock fail!");
        } catch (Throwable throwable) {
            throw new LockException();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
