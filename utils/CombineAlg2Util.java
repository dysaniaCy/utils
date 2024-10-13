package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.yiyouliao.autoprod.common.exception.BizException;
import com.yiyouliao.autoprod.liaoyuan.entity.domain.CombineAlgParamDO;
import com.yiyouliao.autoprod.liaoyuan.mapper.CombineParamMapper;
import com.yiyouliao.autoprod.liaoyuan.mapper.CombineResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hongmao.xi
 * @date 2024-05-22 14:24
 **/
@Slf4j
@Component
public class CombineAlg2Util {

    @Resource
    private CombineParamMapper combineParamMapper;
    @Resource
    private CombineResultMapper combineResultMapper;

    public CombineAlgParamDO matchParam(Integer sceneNum, Integer requireNum) {
        // 最大重复镜头数，重复度设为40%
        int duplicateNum = (int) Math.floor(sceneNum * 0.4D);
        if (duplicateNum < 1) { // 分镜完全不重，意味着需要生产多少视频，每个分镜就需要多少个素材
            CombineAlgParamDO combineParam = new CombineAlgParamDO();
            combineParam.setMaterialNum(requireNum);
            return combineParam;
        }
        return combineParamMapper.selectOne(Wrappers.<CombineAlgParamDO>lambdaQuery().eq(CombineAlgParamDO::getSceneNum, sceneNum)
                .eq(CombineAlgParamDO::getDuplicateNum, duplicateNum)
                .ge(CombineAlgParamDO::getCombineNum, requireNum)
                .last(" limit 1"));
    }

    public CombineAlgParamDO getParam(List<Integer> materialQuantityList, double duplicateRate) {
        // 取分镜中最小的素材数，算法预跑出的数据都是以每个分镜素材数一致为前提
        Integer sceneMinMaterialNum = Collections.min(materialQuantityList);
        if (sceneMinMaterialNum == 0) {
            CombineAlgParamDO param = new CombineAlgParamDO();
            param.setCombineNum(0);
            return param;
        }
        int sceneNum = materialQuantityList.size();
        int duplicateSceneNum = (int) Math.floor(sceneNum * duplicateRate);
        if (duplicateSceneNum < 1) {
            CombineAlgParamDO param = new CombineAlgParamDO();
            param.setCombineNum(sceneMinMaterialNum);
            return param;
        }
        CombineAlgParamDO combineParam = combineParamMapper.selectOne(Wrappers.<CombineAlgParamDO>lambdaQuery().eq(CombineAlgParamDO::getSceneNum, sceneNum)
                .eq(CombineAlgParamDO::getMaterialNum, sceneMinMaterialNum)
                .eq(CombineAlgParamDO::getDuplicateNum, duplicateSceneNum));
        if (combineParam == null) { // 说明预跑的数据覆盖度不够，需要补充
            log.error("can not find combine alg param, {}", JSONUtil.toJsonStr(materialQuantityList));
            throw new BizException("参数异常");
        }
        return combineParam;
    }

    public List<List<Integer>> getPermutation(List<Integer> materialQuantityList, Integer requireNum, double duplicateRate) {
        Integer sceneMinMaterialNum = Collections.min(materialQuantityList);
        if (sceneMinMaterialNum == 0) {
            return Lists.newArrayList();
        }
        CombineAlgParamDO combineParam = getParam(materialQuantityList, duplicateRate);
        if (combineParam.getId() == null) { // 为空表示分镜完全不重的情况
            return combineNoDuplicate(materialQuantityList, requireNum);
        }
        List<String> perms = combineResultMapper.selectPermutation(combineParam.getId());
        if (requireNum < perms.size()) {
            // 从算法预跑出的组合中随机取
            perms = ListUtils.random(perms, requireNum);
        }
        return perms.stream().map(item -> JSONUtil.toList(item, Integer.class)).collect(Collectors.toList());
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
}
