package com.chao;

import com.chao.entity.User;
import com.chao.utils.JedisPoolUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import redis.clients.jedis.*;

import java.util.*;

@SpringBootTest
class SpringbootRedisApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    private HashMap<Object, String> convertObject2JsonString(Set<User> users) {
        HashMap<Object, String> jsonStringMap = new HashMap();
        for (User user : users) {
            try {
                String userString = objectMapper.writeValueAsString(user);
                jsonStringMap.put(user, userString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return jsonStringMap;
    }

    @Test
    void contextLoads() throws JsonProcessingException {
        System.out.println(redisTemplate);
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection connection = connectionFactory.getConnection();
        connection.multi();
        // 操作字符串
        ValueOperations<String, Object> stringOps = redisTemplate.opsForValue();
        // 操作list
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        // 操作set
        SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
        // 操作zset
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        // 操作hash
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();

        User user01 = new User("Qiqi", 18, "gender");
        User user02 = new User("yangyuqi", 22, "gender");
        User user03 = new User("konglingchao", 18, "gender");

        HashSet<User> users = new HashSet<>();
        users.add(user01);
        users.add(user02);
        users.add(user03);

        HashMap<Object, String> objectStringHashMap = convertObject2JsonString(users);

        Set<Map.Entry<Object, String>> entries = objectStringHashMap.entrySet();
        int userNum = 0;

        for (Map.Entry<Object, String> entry : entries) {
            opsForHash.put("users", "user" + String.valueOf(userNum), entry.getValue());
            userNum++;
        }

        //System.exit(0);

        Set<String> keys = redisTemplate.keys("*");

        for (String key : keys) {
            DataType dataType = redisTemplate.type(key);
            switch (dataType) {
                case STRING:
                    System.out.println(stringOps.get(key));
                    break;
                case LIST:
                    System.out.println(listOperations.range(key, 0, -1));
                    break;
                case SET:
                    System.out.println(opsForSet.members(key));
                    break;
                case ZSET:
                    System.out.println(zSetOperations.range(key, 0, 1));
                    break;
                case HASH:
                    Set<Object> hashKeys = opsForHash.keys(key);
                    for (Object hashKey : hashKeys) {
                        Object hashValue = opsForHash.get(key, hashKey);
                        System.out.println(key + "->" + hashKey + "->" + hashValue);
                    }
                    break;
                default:
                    break;
            }
        }
        connection.close();
    }

  /*  @Test
    public void testTx() {
        System.out.println(redisTemplate);
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {

            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 运行时异常 只有那一条命令不会执行成功
                operations.multi();
                operations.opsForValue().set("name", "konglc");
                operations.opsForValue().set("count", "19000");
                operations.opsForValue().increment("count");
                List exec = operations.exec();
                return exec;
            }
        };
        Object execute = redisTemplate.execute(sessionCallback);
        System.out.println(redisTemplate.opsForValue().get("name"));
        System.out.println(execute);
    }*/

    public boolean doSecKill(String uid, String procId) {
        if (uid == null || procId == null) {
            return false;
        }
        // 库存key
        String stockKey = "stock" + procId + "qt:";
        System.out.println("stockKey : " + stockKey);
        // 秒杀成功的用户的key
        String userKey = "user : " + uid;
        // 获取库存
        String stockCount = String.valueOf(redisTemplate.opsForValue().get(stockKey));
        if (stockCount == null) {
            System.out.println("秒杀还没开始");
            return false;
        }

        // 判断用户是否有重复秒杀操作
        if (redisTemplate.opsForSet().isMember(userKey, uid)) {
            System.out.println("已经秒杀成功,不能重复秒杀");
            return false;
        }

        // 判断商品数量 库存小于0 秒杀结束
        if (Integer.parseInt(stockCount) < 0) {
            System.out.println("秒杀已经结束了....");
            return false;
        }

        // 减少库存
        redisTemplate.opsForValue().decrement(stockKey);
        redisTemplate.opsForSet().add(userKey, uid);

        Integer ck = (Integer) redisTemplate.opsForValue().get(stockKey);
        System.out.println("ck :" + ck);
        return false;
    }

    public String getUserId() {
        String userId="";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int j = random.nextInt(10);
            userId+=String.valueOf(j);
        }
        return userId;
    }

    @Test
    public void testSecKill() {
        for (int i = 0; i < 1000 ; i++) {
                doSecKill(getUserId(),"1000");
        }
    }

    @Test
    public void testOps(){
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }

    @Test
    public void testJedis(){

        HostAndPort ipPort = new HostAndPort("192.168.10.133",6379);

        Jedis jedis = new Jedis(ipPort);

        String ping = jedis.ping();

        System.out.println(ping);

        Long res = jedis.setnx("name".getBytes(), "YangYuQi".getBytes());

        System.out.println(res);
        String name = jedis.get("name");
        System.out.println(name);
    }

    @Test
    public void testJedisPool(){
        JedisPool poolInstance = JedisPoolUtils.getJedisPoolInstance();
        Jedis jedis = poolInstance.getResource();
        Transaction transaction = jedis.multi();

        transaction.set("acctA",String.valueOf(2000));
        transaction.set("acctB", String.valueOf(10000));

        transaction.incrBy("acctA",4000);
        // int i = 1/ 0;
        transaction.decrBy("acctB",4000);

        Response<String> balanceA = transaction.get("acctA");
        Response<String> balanceB = transaction.get("acctB");

        System.out.println(balanceA.toString());
        System.out.println(balanceB.toString());

        List<Object> objects = transaction.exec();

        for (Object object : objects) {
            System.out.println(object);
        }
    }
}