package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * @author hongmao.xi
 * @date 2023-08-03 15:33
 **/
public class AlgUtil {

    public static int[] scale(int origWidth, int origHeight, int targetWidth, int targetHeight){
        float origRatio = (float) origWidth / origHeight;
        float targetRatio = (float) targetWidth / targetHeight;

        int scaledWidth, scaledHeight;
        boolean isWidthTarget = origRatio < targetRatio;

        if (isWidthTarget) {
            // 原图宽高比较大，以宽为准进行等比例缩放
            scaledWidth = targetWidth;
            scaledHeight = scaledWidth * origHeight / origWidth;
        } else {
            // 原图宽高比较小，以高为准进行等比例缩放
            scaledHeight = targetHeight;
            scaledWidth = scaledHeight * origWidth / origHeight;
        }

        return new int[]{scaledWidth, scaledHeight, isWidthTarget ? 1 : 0};
    }

    public static int[] scaleBasic(int origWidth, int origHeight, int targetWidth, int targetHeight) {
        int scaledWidth, scaledHeight, posX = 0, posY = 0;

        if ((double) origWidth / origHeight > (double) targetWidth / targetHeight) {
            scaledWidth = targetWidth;
            scaledHeight = (int) Math.round((double) targetWidth * origHeight / origWidth);
        } else {
            scaledHeight = targetHeight;
            scaledWidth = (int) Math.round((double) targetHeight * origWidth / origHeight);
        }

        //计算posX
        if (scaledWidth < targetWidth) {
            int blankWidth = targetWidth - scaledWidth;
            posX = blankWidth / 2;
        }

        //计算posY
        if (scaledHeight < targetHeight) {
            int blankHeight = targetHeight - scaledHeight;
            posY = blankHeight / 2;
        }
        return new int[]{scaledWidth, scaledHeight, posX, posY};
    }

    public static float[] scaleBasicForAli(int origWidth, int origHeight, int targetWidth, int targetHeight) {
        float width = 1, height = 1, x = 0, y = 0;

        if ((double) origWidth / origHeight > (double) targetWidth / targetHeight) {
            int scaledHeight = (int) Math.round((double) targetWidth * origHeight / origWidth);
            height = (float) scaledHeight / targetHeight;
            y = (1 - height) / 2;
        } else {
            int scaledWidth = (int) Math.round((double) targetHeight * origWidth / origHeight);
            width = (float) scaledWidth / targetWidth;
            x = (1 - width) / 2;
        }
        return new float[]{width, height, x, y};
    }


    public static Float longToFloat(long value) {
        return (float) value / 1000.0f;
    }

    /**
     * 根据分辨率获取视频指定宽高
     */
    public static int[] getWidthAndHeight(String resolution) {
        int width = 1080, height = 1920;
        if (StringUtils.isNotBlank(resolution)) {
            String[] split = StringUtils.split(resolution, "x");
            width = Integer.parseInt(split[0]);
            height = Integer.parseInt(split[1]);
        }
        return new int[]{width, height};
    }

    public static int[] getWidthAndHeightByAttach(String size) {
        int width = 500, height = 400;
        if (StringUtils.isNotBlank(size)) {
            String[] split = StringUtils.split(size, "*");
            width = Integer.parseInt(split[0]);
            height = Integer.parseInt(split[1]);
        }
        return new int[]{width, height};
    }

    public static float getWidthAndHeightPercent(int origin, int target) {
        return BigDecimal.valueOf(origin / (float) target).setScale(4, RoundingMode.UP).floatValue();
    }

    /**
     * 计算环比增长率的方法
     * （本期统计周期数据 - 上期统计周期数据）% 上期统计周期数据 x 100%
     * @param currentPeriodData 本期数据
     * @param previousPeriodData 上期数据
     * @return 环比增长率, 如果上期数据为0且本期数据不为0则返回100，都为0返回0
     */
    public static double calculateSequentialGrowthRate(Long currentPeriodData, Long previousPeriodData) {
        if (previousPeriodData == 0) {
            return (currentPeriodData == 0) ? 0 : 100;
        }
        BigDecimal currentData = BigDecimal.valueOf(currentPeriodData);
        BigDecimal previousData = BigDecimal.valueOf(previousPeriodData);
        BigDecimal growthRate = currentData.subtract(previousData)
                .divide(previousData, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        return growthRate.doubleValue();
    }

    /**
     * 根据前端配置计算后端字体大小
     */
    public static int getFontSizeByWeb(int feFontSize, int feWidth, int feHeight, int outWidth, int outHeight) {
        return BigDecimal.valueOf(feFontSize / Math.max(getWidthAndHeightPercent(feWidth, outWidth), getWidthAndHeightPercent(feHeight, outHeight))).intValue();
    }

    public static void main(String[] args) {
        System.out.println(getFontSizeByWeb(28, 275, 520, 1080, 1920));
    }
}
