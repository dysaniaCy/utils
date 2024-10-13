package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiedong
 * @Description:   文本转换
 * @dateTime: 2023/9/18 17:21
 **/
public class TextProcessUtil {

    public static List<String> textSymbolTranslation(List<String> list) {
        List<String> result = new ArrayList<>();
        for (String text : list) {
            result.add(translationString(text));
        }
        return result;
    }

    public static List<List<String>> textColumnTranslation(List<List<String>> list) {
        List<List<String>> result = new ArrayList<>();
        for (List<String> listText : list) {
            List<String> str = new ArrayList<>();
            for (String s : listText) {
                str.add(translationString(s));
            }
            result.add(str);
        }
        return result;
    }

    public static String SymbolTranslation(String text) {
        return translationString(text);
    }

    public static String translationString(String text) {
        if (StrUtil.isBlank(text)){
            return "";
        }
        String result = text;
        text = text.replace("，", "\n");
        text = text.replace("。", "\n");
        text = text.replace("？", "\n");
        result = text.replace("\\n", "\n");
        return result;
    }


}
