package com.chao.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Mr chaochao
 * @version 1.0
 * @date 2022/3/13 19:31
 */
public class JedisPoolUtils {
    private static volatile JedisPool jedisPool = null;

    private JedisPoolUtils() {

    }

    public static JedisPool getJedisPoolInstance() {
        if (jedisPool == null) {
            synchronized (JedisPool.class) {
                if (jedisPool == null) {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    // 最大连接数
                    jedisPoolConfig.setMaxTotal(200);
                    // 最大连接空闲数
                    jedisPoolConfig.setMaxIdle(200);
                    // 最大等待时间
                    jedisPoolConfig.setMaxWaitMillis(100 * 1000);

                    //
                    jedisPoolConfig.setBlockWhenExhausted(true);
                    jedisPoolConfig.setTestOnBorrow(true);
                    jedisPool = new JedisPool(jedisPoolConfig, "192.168.10.133", 6379, 60000);
                }
            }
        }
        return jedisPool;
    }

    public static void release(JedisPool jedisPool, Jedis jedis) {
        if (jedis == null) {
            jedisPool.returnResource(jedis);
        }
    }
}
