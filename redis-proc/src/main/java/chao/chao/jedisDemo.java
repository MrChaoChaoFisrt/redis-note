package chao.chao;

import redis.clients.jedis.Jedis;

/**
 * @author Mr chaochao
 * @version 1.0
 * @date 2022/3/8 23:01
 */

public class jedisDemo {
    public static void main(String[] args) {
        // 创建jedis的对象
        Jedis jedis = new Jedis("192.168.10.133",6379);
        String value = jedis.ping();
        System.out.println(value);
    }
}
