package com.yiyouliao.autoprod.liaoyuan.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @Author 陈阳
 * @Date 2023/11/23 15:53
 **/
public class ScriptTextUtil {

    /**
     * 获取中英文字符长度
     * @param value 字符串
     * @return 字符串长度
     */
    public static int transformCoding(String value) {
        try {
            return new String(value.getBytes("GB2312"), StandardCharsets.ISO_8859_1).length() / 2;
        } catch (UnsupportedEncodingException e) {
            return getChineseCharLength(value) / 2;
        }
    }

    public static boolean isChineseChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static boolean isEnglishChar(char c) {
        // 使用 Unicode 字符集中的代码范围来判断字符是否为英文字符、标点符号或空格
        return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= 0x0021 && c <= 0x007E) // ASCII 中的标点符号和数字
                || (c == 0x0020)) // 空格
                || (c == 0x000A); // 换行符;
    }

    public static int getChineseCharLength(String str) {
        int length = 0;
        char[] chars = str.toCharArray();
        for (char c : chars) {
            if (isChineseChar(c)) {
                length += 2;
            } else if (isEnglishChar(c)) {
                length += 1;
            }
        }
        return length;
    }

    /**
     * 判断文案是否为纯英文
     * 依据：一个中文占两个字节，一个英文占一个字节。故若是字符长度与字节长度相等，则判断为英文。否则为中文。
     */
    public static boolean isEnglish(String text) {
        byte[] bytes = text.getBytes();
        int i = bytes.length;//i为字节长度
        int j = text.length();//j为字符长度
        return i == j;
    }

}