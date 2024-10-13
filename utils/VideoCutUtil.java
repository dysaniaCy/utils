package com.yiyouliao.autoprod.liaoyuan.utils;

import com.yiyouliao.autoprod.component.redis.service.RedisService;
import com.yiyouliao.autoprod.liaoyuan.entity.domain.CommonMaterialDO;
import com.yiyouliao.autoprod.liaoyuan.entity.request.aigc.VideoCutRequest;
import com.yiyouliao.autoprod.liaoyuan.enums.VideoCutTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static com.yiyouliao.autoprod.liaoyuan.constants.RedisKeyConstant.VIDEO_UPLOAD_CUT_TYPE;

/**
 * @Author 陈阳
 * @Date 2024/2/19 17:10
 **/
@Component
@Slf4j
public class VideoCutUtil {

    @Resource
    private RedisService redisService;

    private static RedisService redisServiceStatic;

    @PostConstruct
    public void init() {
        redisServiceStatic = redisService;
    }

    public static void setCutTypeToRedis(String id, Integer cutType) {
        redisServiceStatic.setCacheObject(VIDEO_UPLOAD_CUT_TYPE + id, cutType, 1L, TimeUnit.DAYS);
    }

    public static VideoCutRequest commonMaterial2VideoCutRequest(CommonMaterialDO material) {
        VideoCutRequest request = new VideoCutRequest();
        request.setInfoId(material.getMaterialId());
        request.setUrl(material.getTransVideo() == null ? null : material.getTransVideo().getUrl());
        Integer cutType = redisServiceStatic.getCacheObject(VIDEO_UPLOAD_CUT_TYPE + material.getMaterialId());
        request.setTaskType(cutType == null ? VideoCutTypeEnum.CUT_DETECTION.getValue() : cutType);
        VideoCutRequest.Meta meta = new VideoCutRequest.Meta();
        meta.setDuration(material.getTransVideo().getDuration());
        meta.setDeptId(Long.valueOf(material.getDeptId()));
        meta.setAccountId(material.getCreateBy());
        request.setMeta(meta);
        return request;
    }

}
