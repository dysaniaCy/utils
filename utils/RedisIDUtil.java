package com.yiyouliao.autoprod.liaoyuan.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author xieDong
 * @date 2022/9/4
 * @apiNote
 */

@Component
public class RedisIDUtil {

    private static StringRedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    private void init(){
        redisTemplate = stringRedisTemplate;
    }

    /**
     * 初试时间
     */
    private static final long BEGIN_TIMESTAMP = 1640995200;

    /**
     * 切换位数
     */
    private static final long CHANGE_BIT = 36;


    public static long nextLongId(String keyPreFix) {
        LocalDateTime now = LocalDateTime.now();
        // 生成时间搓
        long nowTime = now.toEpochSecond(ZoneOffset.UTC);

        long timestamp = nowTime - BEGIN_TIMESTAMP;
        // 每天新增一个key
        String format = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));

        Long increment = redisTemplate.opsForValue().increment("icr:" + keyPreFix + ":" + format);

        //拼接并返回 将时间往前挪动32位，那么原来的 后 32 就都是0 然后将32的步数做一个或处理  总共64位 前三十二位是时间搓 后三十二位是自增值
        return timestamp << CHANGE_BIT | increment;
    }

    public static String nextStringId(String keyPreFix) {
       return keyPreFix + nextLongId(keyPreFix);
    }


}
