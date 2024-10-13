package com.yiyouliao.autoprod.liaoyuan.utils;

import java.util.*;

/**
 * @author Canda
 */
public class DYTextUtils {
    private static Map<String, String> sensitiveWordsMap = null;
    private static Integer minMatchType = 1;      //最小匹配规则

    static {
        sensitiveWordsMap = new SensitiveWordsInit().initSensitiveWords();
    }

    /**
     * 判断文本是否包含关键字
     * @param txt 待判断文本
     * @param matchType 匹配类型： 1 最小匹配原则；2 最大匹配原则
     * @return true 包含；false 不包含
     */
    public static Boolean isContainsSensitiveWords(String txt, Integer matchType){
        if(txt == null || "".equals(txt)){
            return false;
        }
        for(int i = 0; i < txt.length();i++){
            Integer matchFlag = checkSensitiveWords(txt, i , matchType);
            if(matchFlag > 0){
                return true;
            }
        }
        return false;
    }

    /**
     * 校验是否包含敏感词
     * @param txt 待判断文本
     * @param start 起始位置
     * @param matchType 匹配类型： 1 最小匹配原则；2 最大匹配原则
     * @return 大于0表示包含敏感词且表示敏感词匹配长度，否则不包含
     */
    private static Integer checkSensitiveWords(String txt, Integer start, Integer matchType) {
        //敏感词结束标志
        Boolean flag = false;
        char word;
        //匹配标识数默认为0
        Integer matchFlag = 0;
        Map childMap = sensitiveWordsMap;
        for(int i = start;i < txt.length();i++){
            word = txt.charAt(i);
            childMap = (Map)childMap.get(word);
            if(childMap == null){
                //不存在该字打头的敏感词
                break;
            } else {
                //匹配标识+1
                matchFlag++;
                if("1".equals(childMap.get("isEnd"))){
                    flag = true;
                    if(minMatchType.equals(matchType)){
                        //最小匹配规则则跳出，否则继续匹配
                        break;
                    }
                }
            }
        }
        if(matchFlag < 2 || !flag){
            //匹配长度需大于2才为词，并且敏感词已结束
            matchFlag = 0;
        }
        return matchFlag;
    }

    /**
     * 获取所有敏感词
     * @param txt 待判断文本
     * @param matchType 匹配类型： 1 最小匹配原则；2 最大匹配原则
     * @return
     */
    public static Set<String> getSensitiveWords(String txt,Integer matchType){
        Set<String> sensitiveWords = new HashSet<>();

        for(int i = 0;i < txt.length();i++){
            Integer length = checkSensitiveWords(txt, i, matchType);
            if(length > 0){
                sensitiveWords.add(txt.substring(i, i + length));
                //循环i会+1，所以需-1
                i = i + length - 1;
            }
        }
        return sensitiveWords;
    }

    /**
     * 展示敏感词
     * @param txt 文本
     * @param matchType 匹配类型： 1 最小匹配原则；2 最大匹配原则
     * @return 处理后的文本
     */
    public static List<String> showSensitiveWords(String txt, Integer matchType){
        List<String> sWords = new ArrayList<>();
        if(txt == null || "".equals(txt)){
            return sWords;
        }
        //获取所有敏感词
        Set<String> sensitiveWords = getSensitiveWords(txt, matchType);
        Iterator<String> iterator = sensitiveWords.iterator();
        while (iterator.hasNext()){
            String sWord = iterator.next();
            sWords.add(sWord);
        }
        return sWords;
    }
}
