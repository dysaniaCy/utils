package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 裂变算法
 *
 * @author hongmao.xi
 * @date 2023-09-26 11:08
 **/
@Slf4j
public class CombineAlgUtils {

    static int bitCount(int x) {
        int cnt = 0;
        while((x & -x) != 0){
            cnt++;
            x &= x - 1;
        }
        return cnt;
    }

    static int ary2bitmsk(List<Integer> video, List<Integer> candidatesPrefixSum){
        int bitmsk = 0;
        for(int i = 0; i < video.size(); i++){
            bitmsk |= (1 << (candidatesPrefixSum.get(i) + video.get(i)));
        }
        return bitmsk;
    }

    static List<Integer> genSeq(List<Integer> candidatesLi, int numSegs){
        Random random = new Random();
        List<Integer> seq = new ArrayList<>();
        for(int i = 0; i < numSegs; i++){
            seq.add(random.nextInt(candidatesLi.get(i)));
        }
        return seq;
    }

    public static List<List<Integer>> getPermutation(List<Integer> candidatesList, double duplicateRate, int numTargets, int tryMultiple){
        int numDuplicateSegs = (int) Math.floor(candidatesList.size() * duplicateRate);

        // 完全不重复，则最大组合数为各分镜中的最少素材分解的素材数
        if (numDuplicateSegs == 0) {
            return combineNoDuplicate(candidatesList, numTargets);
        }

        // 组合数能穷举，则走穷举再比较重复度的逻辑
        if (canListAll(candidatesList)) {
            return combineByListAll(candidatesList, numTargets, numDuplicateSegs);
        }

        // 其余走随机出组合再比较重复度的逻辑
        int cnts = 0;
        // 尝试次数
        long numgens = 0L;
        List<Integer> candidatesPrefixSum = new ArrayList<>();
        Integer sum = 0;
        candidatesPrefixSum.add(0);
        for(Integer candidate : candidatesList.subList(0, candidatesList.size() - 1)){
            sum += candidate;
            candidatesPrefixSum.add(sum);
        }
        List<Integer> bitmsks = new ArrayList<>();
        List<List<Integer>> ans = new ArrayList<>();
        while (cnts < numTargets){
            List<Integer> video = genSeq(candidatesList, candidatesList.size());
            int bitform = ary2bitmsk(video, candidatesPrefixSum);
            boolean isBreak = false;
            for(Integer bmsk : bitmsks){
                if(bitCount(bmsk & bitform) > numDuplicateSegs){
                    isBreak = true;
                    break;
                }
            }
            numgens++;
            if (numgens > (long) 10000 * tryMultiple) {
                break;
            }
            if(isBreak) {
                continue;
            }
            bitmsks.add(bitform);
            ans.add(video);
            cnts++;

        }
        log.info("生成{}条满足条件的数据，共尝试了{}次", cnts, numgens);
        return ans;
    }

    private static List<List<Integer>> combineNoDuplicate(List<Integer> candidateList, int numTargets) {
        Integer combSize = Collections.min(candidateList);
        List<List<Integer>> combineList = Lists.newArrayListWithExpectedSize(combSize);
        List<List<Integer>> candidateMemberList = Lists.newArrayList();
        for (Integer candidate : candidateList) {
            List<Integer> memberList = Lists.newArrayList();
            for (int i = 0; i < candidate; i++) {
                memberList.add(i);
            }
            candidateMemberList.add(memberList);
        }
        for (int i = 0; i < combSize; i++) {
            List<Integer> combine = Lists.newArrayListWithExpectedSize(candidateList.size());
            for (int j = 0; j < candidateList.size(); j++) {
                combine.add(ListUtils.randomGetAndRemove(candidateMemberList.get(j)));
            }
            combineList.add(combine);

        }
        if (combineList.size() > numTargets) {
            Collections.shuffle(combineList);
            combineList = combineList.subList(0, numTargets);
        }
        return combineList;
    }

    /**
     * 在数据量不大的情况下，走穷举的逻辑筛选出满足重复度的组合
     */
    private static List<List<Integer>> combineByListAll(List<Integer> candidateList, int targetNum, int numDuplicateSegs) {
        List<List<Integer>> candidateMemberList = Lists.newArrayList();
        for (Integer candidate : candidateList) {
            List<Integer> memberList = Lists.newArrayList();
            for (int i = 0; i < candidate; i++) {
                memberList.add(i);
            }
            candidateMemberList.add(memberList);
        }
        List<List<Integer>> combineList = Lists.cartesianProduct(candidateMemberList);

        List<Integer> candidatesPrefixSum = new ArrayList<>();
        Integer sum = 0;
        candidatesPrefixSum.add(0);
        for(Integer candidate : candidateList.subList(0, candidateList.size() - 1)){
            sum += candidate;
            candidatesPrefixSum.add(sum);
        }
        List<Integer> bitmsks = new ArrayList<>();
        List<List<Integer>> returnList = new ArrayList<>();
        for (List<Integer> video : combineList) {
            int bitform = ary2bitmsk(video, candidatesPrefixSum);
            boolean isBreak = false;
            for(Integer bmsk : bitmsks){
                if(bitCount(bmsk & bitform) > numDuplicateSegs){
                    isBreak = true;
                    break;
                }
            }
            if(isBreak) {
                continue;
            }
            bitmsks.add(bitform);
            returnList.add(video);
        }
        // 打散一下
        Collections.shuffle(returnList);
        if (returnList.size() > targetNum) {
            returnList = returnList.subList(0, targetNum);
        }
        return returnList;
    }

    public static boolean canListAll(List<Integer> candidatesList) {
        long product = 1L;
        for (Integer candidates : candidatesList) {
            product = product * candidates;
            if (product > 10000000) { // 阈值：1000万
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        long t = System.currentTimeMillis();

        int numTargets = 10000;
        double duplicateRate = 0.4d;

//        List<Integer> candidatesLi = new Random().ints(numSegs, 10, 20).boxed().collect(Collectors.toList());
        List<Integer> candidatesLi = Lists.newArrayList(63, 40, 21, 20, 21, 24, 21, 22, 35);
//        9, 5, 3, 6, 3);

        List<List<Integer>> list = getPermutation(candidatesLi, duplicateRate, numTargets, 1000);

        t = (System.currentTimeMillis() - t);
        for (List<Integer> l : list) {
            System.out.println(JSONUtil.toJsonStr(l));
        }

        Map<Integer, Map<String, Integer>> statMap = Maps.newLinkedHashMap();
        for (int i = 0; i < list.size(); i++) {
            List<Integer> get = list.get(i);
            for (int j = 0; j < get.size(); j++) {
                Integer select = get.get(j);
                if (statMap.containsKey(j)) {
                    Map<String, Integer> materialMap = statMap.get(j);
                    materialMap.put(String.valueOf(select), materialMap.getOrDefault(String.valueOf(select), 0) + 1);
                } else {
                    Map<String, Integer> map = Maps.newHashMap();
                    map.put(String.valueOf(select), 1);
                    statMap.put(j, map);
                }
            }
        }
        log.info("combine stat: {} \n", JSONUtil.toJsonPrettyStr(statMap));
        System.out.printf("运行用时：%dms\n", t);
    }
}
