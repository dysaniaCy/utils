package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.core.util.StrUtil;

/**
 * @Author：xiedong
 * @name：LoggingUtils
 * @Date：2023/7/11 16:51
 */
public class LoggingUtils {
    private static final ThreadLocal<String> logLocal = new ThreadLocal<>();


    public static boolean printLogging() {
        return StrUtil.isBlank(logLocal.get());
    }

    public static void notPrintLog() {
        logLocal.set("false");
    }

    public static void remove() {
        logLocal.remove();
    }
}
