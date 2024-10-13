package com.yiyouliao.autoprod.liaoyuan.utils;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author ld
 * @date 2022/8/29
 */
public class UserContext {
    private static final ThreadLocal<Map<String,String>> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String key,String obj){
        getLocalMap().put(key,obj);
    }

    private static Map<String,String> getLocalMap() {
        Map<String, String> map = THREAD_LOCAL.get();
        if (map == null){
            map = new ConcurrentHashMap<>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static String get(String key){
        return getLocalMap().getOrDefault(key,null);
    }

    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
