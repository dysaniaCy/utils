package com.yiyouliao.autoprod.liaoyuan.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 拖动排序
 * @Author 陈阳
 * @Date 2024/1/9 11:29
 **/

public class DragSortUtil {

    //最小排序值
    private static final int MIN_SORT_ID = 1;

    //默认的排序值(65536)
    private static final int DEFAULT_SORT_ID = 1 << 16;

    //最大index数(确保sortId值不会超过Integer.MAX_VALUE)
    private static final int MAX_INDEX_NUM = 32767;

    /**
     * @description  创建新的排序值
     * @author  陈阳
     * @date    2024/1/9 16:04
     * @param    index 元素位置或上一顺序的值
     *  @param   isNew 是否为文件夹新增
     * @return  int
    */
    public static int createSortId(int index, boolean isNew) {
        try {
            if (isNew) {
                //当元素个数超过最大index时，需要重新设置排序值
                if (index > MAX_INDEX_NUM) {
                    return 0;
                }
                return DEFAULT_SORT_ID * (index + 1);
            } else {
                return index + DEFAULT_SORT_ID;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @description  插入排序值
     * @author  陈阳
     * @date    2024/1/9 16:05
     * @param    lastSort  前一个位置的排序值
     * @param    nextSort  后一个位置的排序值
     * @return  int
    */
    public static int insertSortId(int lastSort, int nextSort) {
        int sort = (lastSort + nextSort) / 2;
        //当排序值小于最小排序值，需要重新设置排序值
        if (sort < MIN_SORT_ID) {
            return 0;
        }
        return sort;
    }

    /**
     * @description  重新设置排序值
     * @author  陈阳
     * @date    2024/1/9 16:06
     * @param    size    集合大小
     * @return  java.util.List<java.lang.Integer>
    */
    public static List<Integer> reloadSortId(int size) {
        List<Integer> list = new ArrayList<>(size);
        int cardinalSort = DEFAULT_SORT_ID;
        if (size > MAX_INDEX_NUM) {
            cardinalSort = DEFAULT_SORT_ID / 2;
        }
        for (int i = 1; i <= size; i++) {
            list.add(cardinalSort * i);
        }
        return list;
    }

}
