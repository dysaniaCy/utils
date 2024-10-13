package com.yiyouliao.autoprod.liaoyuan.utils;

/**
 * @author: xiedong
 * @dateTime: 2023/8/16 10:27
 * 数值计算 工具类
 **/
public class CalculateUtil {

    private static final double doubleDiff = 0.01D;

    private static final float floatDiff = 0.1F;

    /**
     * double 数值计算是否相等
     *
     * @param one
     * @param two
     * @return
     */
    public static boolean doubleEquals(double one, double two) {
        return Math.abs(one - two) < doubleDiff;
    }

    public static boolean doubleEquals(double one, double two, double diff) {
        return Math.abs(one - two) < diff;
    }

    /**
     * float 数值计算是否相等
     *
     * @param one
     * @param two
     * @return
     */
    public static boolean floatEquals(float one, float two) {
        return Math.abs(one - two) < floatDiff;
    }

    public static boolean floatEquals(float one, float two, float diff) {
        return Math.abs(one - two) < diff;
    }

    /**
     *  不足一秒为一秒 大于一秒为四色五入
     * @param source
     * @return
     */
    public static long roundedMilliseconds(Long source) {

        long result = Math.round(source / 1000.0) * 1000;
        if (result < 1000) {
            result = 1000; // 如果小于一秒，则返回一秒的毫秒值
        }
        return result;
    }

}
