package com.yiyouliao.autoprod.liaoyuan.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Canda
 */
@Slf4j
public class SensitiveWordsInit {
    public HashMap sensitiveWordsMap;

    /**
     * 初始化敏感词HashMap
     */
    public Map initSensitiveWords(){
        try {
            //读取敏感词文件
            Set<String> sensitiveWordSet = readSensitiveWordsFile();
            //将敏感词加入HashMap
            addSensitiveWordsToHashMap(sensitiveWordSet);

        } catch (Exception e){
            e.printStackTrace();
        }
        return sensitiveWordsMap;
    }

    /**
     * 将敏感词加入HashMap
     */
    private void addSensitiveWordsToHashMap(Set<String> sensitiveWords) {
        sensitiveWordsMap = new HashMap(sensitiveWords.size());

        String word = null;
        Map childMap = null;
        Map<String,String> newWordMap = null;
        Iterator<String> iterator = sensitiveWords.iterator();
        while (iterator.hasNext()){
            //关键字
            word = iterator.next();
            childMap = sensitiveWordsMap;
            //遍历该关键字
            for (int i = 0; i < word.length();i++){
                char key = word.charAt(i);
                Object wordMap = childMap.get(key);
                if(wordMap != null){
                    childMap = (Map)wordMap;
                } else {
                    newWordMap = new HashMap<>();
                    newWordMap.put("isEnd", "0");
                    childMap.put(key, newWordMap);
                    //指向当前map,继续遍历
                    childMap = newWordMap;
                }

                if(i == word.length() - 1){
                    //最后一个
                    childMap.put("isEnd", "1");
                }
            }
        }
    }

    /**
     * 读取敏感词文件
     */
    private Set<String> readSensitiveWordsFile() {

        InputStreamReader isReader = null;
        try {
            isReader = new InputStreamReader(this.getClass().getResourceAsStream("/" + "DYSensitiveWords.txt"), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isReader);
            String s = br.readLine();
            if(s != null && !"".equals(s)){
                Set<String> set = new HashSet<>();
                Collections.addAll(set, s.split(","));
                log.info("DYSensitiveWords.txt read success ");
                return set;
            } else {
              log.error("DYSensitiveWords.txt does not exist or the content is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(isReader != null) {
                try {
                    isReader.close();
                } catch ( Exception e){

                }
            }
        }
        return null;
    }
}
