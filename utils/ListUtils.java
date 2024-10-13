package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author hongmao.xi
 * @date 2022-06-20 11:26
 **/
public class ListUtils {

    /**
     * 随机选取list中的一个值
     */
    public static <T> T randomOne(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("list is empty");
        }
        return list.get(list.size() == 1 ? 0 : new Random().nextInt(list.size()));
    }

    /**
     * 从列表中随机选取指定数量的元素
     *
     * @param inputList 原列表
     * @param requireNum 需要选取的元素数量
     *
     * @return 包含随机元素的新列表
     */
    public static <T> List<T> random(List<T> inputList, Integer requireNum) {
        if (CollectionUtils.isEmpty(inputList)) {
            throw new IllegalArgumentException("list is empty");
        }

        // 如果requireNum接近inputList的大小，打乱整个列表，然后选择前requireNum个元素
        if (requireNum > inputList.size() / 2) {
            List<T> shuffledList = new ArrayList<>(inputList);
            Collections.shuffle(shuffledList);
            return shuffledList.subList(0, requireNum);
        }

        // 否则走生成随机值取的逻辑
        List<T> randomPicks = new ArrayList<>(requireNum);
        Random rnd = new Random();
        int inputSize = inputList.size();

        for (int i = 0; requireNum > 0 && i < inputSize; ++i) {
            int remainingElements = inputSize - i;
            int remainingPicks = requireNum;

            if (rnd.nextInt(remainingElements) < remainingPicks) {
                // 有较高的几率选取该索引位置的元素
                randomPicks.add(inputList.get(i));
                requireNum--;
            }
        }
        return randomPicks;
    }

    /**
     * 随机选取list中的一个值，但排除指定list中的元素
     */
    public static <T> T randomOneExclude(List<T> list, List<T> exclude) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("list is empty");
        }
        if (CollectionUtils.isNotEmpty(exclude)) {
            List<T> copyList = ObjectUtil.cloneByStream(list);
            copyList.removeAll(exclude);
            if (CollectionUtils.isNotEmpty(copyList)) {
                return copyList.get(copyList.size() == 1 ? 0 : new Random().nextInt(copyList.size()));
            } else {
                return null;
            }
        }
        return list.get(list.size() == 1 ? 0 : new Random().nextInt(list.size()));
    }

    public static Integer randomGetAndRemove(List<Integer> list) {
        Integer select = randomOne(list);
        list.remove(select);
        return select;
    }

    public static void main(String[] args) {
        List<Integer> list = Lists.newArrayList(1, 2, 3);
        randomGetAndRemove(list);
        randomGetAndRemove(list);
        randomGetAndRemove(list);
        randomGetAndRemove(list);
        System.out.println(JSONUtil.toJsonStr(list));
    }
}
