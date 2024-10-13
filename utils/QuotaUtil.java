package com.yiyouliao.autoprod.liaoyuan.utils;

import com.yiyouliao.autoprod.liaoyuan.enums.BillType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

/**
 * @author hongmao.xi
 * @date 2024-03-26 11:14
 **/
public class QuotaUtil {

    /**
     * 根据业务类型计算扣减用量
     *
     * @param duration 消耗时长，ms
     * @param billType 消耗业务类型
     * @return 扣除的用量，ms
     */
    public static long calculateQuota(long duration, BillType billType) {

        if (BillType.VIDEO_PROD.equals(billType)) {
            return duration;
        }
        if (BillType.VIDEO_PARSE.equals(billType)) {
            return duration * 4;
        }
        if (BillType.MATERIAL_CUT.equals(billType)) {
            return duration * 2;
        }
        throw new IllegalArgumentException("no match billType: " + billType.value());
    }

    /**
     * 用量格式化，四舍五入
     *
     * @param quota 用量
     * @param timeUnit 需要格式化的时间单位
     * @return
     */
    public static long formatQuota(long quota, TimeUnit timeUnit) {
        if (TimeUnit.SECONDS == timeUnit) {
            return TimeUnit.MILLISECONDS.toSeconds(quota + 500);
        }
        if (TimeUnit.MINUTES == timeUnit) {
            return TimeUnit.MILLISECONDS.toMinutes(quota + 1000 * 30);
        }
        if (TimeUnit.HOURS == timeUnit) {
            return TimeUnit.MILLISECONDS.toHours(quota + 1000 * 60 * 30);
        }
        throw new IllegalArgumentException("not supported timeunit: " + timeUnit.name());
    }

    public static BigDecimal formatQuota(Long quota, TimeUnit timeUnit, int scale) {
        if (TimeUnit.SECONDS == timeUnit) {
            return new BigDecimal(quota).divide(BigDecimal.valueOf(1000), scale, RoundingMode.HALF_UP).stripTrailingZeros();
        }
        if (TimeUnit.MINUTES == timeUnit) {
            return new BigDecimal(quota).divide(BigDecimal.valueOf(1000 * 60), scale, RoundingMode.HALF_UP).stripTrailingZeros();
        }
        if (TimeUnit.HOURS == timeUnit) {
            return new BigDecimal(quota).divide(BigDecimal.valueOf(1000 * 60 * 60), 3, RoundingMode.HALF_UP).stripTrailingZeros();
        }
        throw new IllegalArgumentException("not supported timeunit: " + timeUnit.name());
    }
}
