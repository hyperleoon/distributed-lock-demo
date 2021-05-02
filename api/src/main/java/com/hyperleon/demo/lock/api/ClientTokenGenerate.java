package com.hyperleon.demo.lock.api;

/**
 * ability of client token generate
 *
 * @author leon
 * @date 2021-05-02 19:35
 **/
public interface ClientTokenGenerate {

    /**
     * generate token
     * @return client token
     */
    String generateToken();
}
