package chao.chao;

import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * @author Mr chaochao
 * @version 1.0
 * @date 2022/3/8 23:40
 */
public class JedisInstance {
    private static Jedis jedis = new Jedis("192.168.10.133", 6379);

    public static void main(String[] args) {
        // 模拟验证码发送
        verifyCode("18487074817");

        String s = jedis.get("VerifyCode" + "18487074817" + ":code");
        System.out.println(s);
        jedis.close();
    }

    // 生成6位随机验证码
    public static String getCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            int i1 = random.nextInt(10);
            code += String.valueOf(i1);
        }
        return code;
    }

    // 每个手机每天只能发送三次 验证码放到redis中 设置过期时间
    public static void verifyCode(String phone) {

        // 手机发送的次数
        String countKey = "VerifyCode" + phone + ":count";
        // 验证key
        String codeKey = "VerifyCode" + phone + ":code";

        // 每个手机只能发送三次
        String count = jedis.get(countKey);
        if (count == null) {
            // 设置键值 同时设置过期时间
            jedis.setex(countKey, 24 * 60 * 60, "1");
        } else if (Integer.parseInt(count) <= 2) {
            jedis.incr(countKey);
        } else {
            System.out.println("发送次数超过3次,不能再发送了......");
            return;
        }

        // 发送的验证码放入redis
        String vCode = getCode();
        jedis.setex(codeKey, 120, vCode);
    }

    // 验证校验码
    public static void checkRedisCode(String phone, String code) {
        // 从redis中或验证码
        String codeKey = "VerifyCode" + phone + ":code";
        String redisCode = jedis.get(codeKey);

        //验证
        if (redisCode.equals(code)) {
            System.out.println("成功......");
        } else {
            System.out.println("失败");
        }
    }
}
