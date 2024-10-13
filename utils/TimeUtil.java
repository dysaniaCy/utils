package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created with IntelliJ IDEA.
 *
 * @author ld
 * @date 2022/9/8
 */
public class TimeUtil {

    public static final String YEAR_TO_DAY = "yyyy-MM-dd";

    public static final String YEAR_TO_SECOND = "yyyy-MM-dd HH:mm:ss";

    public static final String NOT_SEP_TIME = " MMdd-HHmm";

    public static final String HIMS_TIME = " MMdd-HHmmss";



    public static String toUtcTime(LocalDateTime localDateTime) {
        // 将LocalDateTime转换为UTC时间的ZonedDateTime
        ZonedDateTime utcDateTime = localDateTime.atZone(ZoneOffset.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);

        // 定义格式化器，注意Z表示固定的UTC（即时区为0）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        // 使用格式化器将ZonedDateTime格式化为字符串
        return utcDateTime.format(formatter);
    }


    public static String toTime(Long second){
        StringBuilder sb = new StringBuilder();
        long m = second/60;
        if (m<10){
            sb.append(0).append(m);
        }else {
            sb.append(m);
        }
        sb.append(":");
        long s = second%60;
        if (s<10){
            sb.append(0).append(s);
        }else{
            sb.append(s);
        }
        return sb.toString();
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String getNowStr(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(new Date());
    }

    public static LocalDateTime dateToLocalDateTime(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String localDateTimeToString(LocalDateTime localDateTime, String format){
        if (ObjectUtil.isNull(localDateTime)){
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

    /**
     *
     * @param number  数据
     * @param decimalDigits 小数位
     * @return
     */
    public static Double formatDouble(double number, int decimalDigits) {
        BigDecimal bd = BigDecimal.valueOf(number);
        bd = bd.setScale(decimalDigits, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void main(String[] args) {
//        for (int i = 0; i < 20; i++) {
//            System.out.println(  generateRandomTimes(LocalDate.of(2023,11,29)));
//        }

        System.out.println(formatDouble(1.34354D, 2));
        System.out.println(formatDouble(1.34354D, 4));

        System.out.println(getNowStr(HIMS_TIME));


    }

    /**
     *  获取当天的高峰期的随机时间
     * @param localDate
     * @return
     */
    public static LocalDateTime  generateRandomTimes(LocalDate localDate) {
         int [][] rangTimeStart = new int[][]{
                 {8,0},{8,0},{8,0},{8,0},{8,0},{8,0},{8,0},{8,0},
                 {11,0},{11,0},{11,0},{11,0},{11,0},
                 {10,30},{10,30},
                 {14,30},{14,30},
                 {18,0},{18,0},{18,0},{18,0},{18,0}, {18,0},{18,0},{18,0},{18,0},{18,0}

         };
         int [][] rangTimeEnd = new int[][]{
                 {10,30},{10,30},{10,30},{10,30},{10,30},{10,30},{10,30},{10,30},
                 {13,30},{13,30},{13,30},{13,30},{13,30},
                 {13,20},{13,20},
                 {18,30},{18,30},
//                 {20,59},{20,59},{20,59},{20,59},{20,59},{20,59},{20,59},{20,59},{20,59},{20,59}
                 {23,59}, {23,59}, {23,59}, {23,59}, {23,59}, {23,59}, {23,59}, {23,59}, {23,59}, {23,59}
         };

        int randomInt = RandomUtil.randomInt(rangTimeStart.length );

        LocalDateTime start =   LocalDateTime.of(localDate, LocalTime.of(rangTimeStart[randomInt][0], rangTimeStart[randomInt][1]));
        LocalDateTime end =   LocalDateTime.of(localDate, LocalTime.of(rangTimeEnd[randomInt][0], rangTimeEnd[randomInt][1]));

        // 计算起始和结束时间之间的分钟数
        long morningMinutesBetween = ChronoUnit.MINUTES.between(start,end);

        // 生成每天的随机时间

         return  generateRandomTime(start, morningMinutesBetween);
    }

    private static LocalDateTime generateRandomTime(LocalDateTime startTime, long minutesBetween) {
        // 生成一定范围内的随机分钟数
        long randomMinutes = ThreadLocalRandom.current().nextLong(minutesBetween);

        // 将随机分钟数添加到起始时间，生成随机时间
        return startTime.plusMinutes(randomMinutes);
    }

}
