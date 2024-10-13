package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.core.util.StrUtil;
import com.yiyouliao.autoprod.liaoyuan.enums.ComponentType;
import com.yiyouliao.autoprod.liaoyuan.enums.MaterialCateEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * @Author 陈阳
 * @Date 2023/11/16 11:49
 **/
@Component
public class MaterialCateUtil {

    @Value("${application.env}")
    private String env;

    private static String staticEnv;

    @PostConstruct
    public void init() {
        staticEnv = env;
    }

    //测试环境不删除的素材类型
    public static ComponentType[] notDeleteList = {ComponentType.ATTACH, ComponentType.AUDIO, ComponentType.PASTER, ComponentType.COVER_STYLE};

    public static String getCateByComponentType(ComponentType componentType) {
        String cate = StrUtil.EMPTY;
        if ("online".equals(staticEnv)) {
            switch (componentType) {
                case ATTACH:
                    cate = MaterialCateEnum.ATTACH.getValue();
                    break;
                case TTS:
                    cate = MaterialCateEnum.TTS.getValue();
                    break;
                case VIDEO:
                    cate = MaterialCateEnum.VIDEO.getValue();
                    break;
                case IMAGE:
                    cate = MaterialCateEnum.IMAGE.getValue();
                    break;
                case AUDIO:
                    cate = MaterialCateEnum.BGM.getValue();
                    break;
                case COVER:
                    cate = MaterialCateEnum.COVER.getValue();
                    break;
                case COVER_STYLE:
                    cate = MaterialCateEnum.COVER_STYLE.getValue();
                    break;
                case PASTER:
                    cate = MaterialCateEnum.PASTER.getValue();
                    break;
            }
        } else {
            //组件素材不可删除，视频素材和tts可以删除
            if (Arrays.asList(notDeleteList).contains(componentType)) {
                cate = MaterialCateEnum.UN_DELETE.getValue();
            } else {
                cate = MaterialCateEnum.CAN_DELETE.getValue();
            }
        }
        return cate;
    }

}
