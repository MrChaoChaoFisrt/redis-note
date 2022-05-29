package com.chao;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Mr chaochao
 * @version 1.0
 * @date 2022/3/8 23:09
 */

public class JedisTest {
    // 操作key
    @Test
    public void testKey() {
        Jedis jedis = new Jedis("192.168.10.133", 6379);
        Set<String> keys = jedis.keys("*");

        for (String key : keys) {
            System.out.println(key + " : " + jedis.type(key));
        }
        jedis.flushDB();
    }

    // 操作String
    @Test
    public void testString() {
        Jedis jedis = new Jedis("192.168.10.133", 6379);
        jedis.flushDB();
        jedis.set("name", "YuQi");
        System.out.println(jedis.get("name"));

        jedis.mset("name", "QiQi", "age", "18");

        for (String key : jedis.keys("*")) {
            System.out.println(key + " : " + jedis.get(key));
        }

    }

    @Test
    public void testHash() {
        Jedis jedis = new Jedis("192.168.10.133", 6379);

        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("name", "YuQi");
        userMap.put("age", "18");
        userMap.put("school", "YanShanUniversity");
        jedis.hset("user:01", userMap);

        Map<String, String> stringMap = jedis.hgetAll("user:01");
        System.out.println(stringMap);
    }

    /**
     * 1、输入手机号，点击发送后随机生成6位数字码，2分钟有效
     * 2、输入验证码，点击验证，返回成功或失败
     * 3、每个手机号每天只能输入3次
     */

    @Test
    public void JedisInstance() {

    }
}