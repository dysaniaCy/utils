package com.yiyouliao.autoprod.liaoyuan.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: xiedong
 * @dateTime: 2023/8/18 11:26
 **/

public class TypeConvertUtil {

    private static final String format = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime StringToLocalDateTimeByFormat(String dateString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateString, formatter);
    }

    public static LocalDateTime stringToLocalDateTime(String dateString) {
        return StringToLocalDateTimeByFormat(dateString, format);
    }

}
