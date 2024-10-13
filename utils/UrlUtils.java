package com.yiyouliao.autoprod.liaoyuan.utils;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: xiedong
 * @dateTime: 2023/9/15 14:12
 **/
public class UrlUtils {

    public static Map<String, String> parseURLParameters(String urlString) {
        Map<String, String> paramsMap = new HashMap<>();

        try {
            URL url = new URL(urlString);
            String query = url.getQuery();

            if (query != null && !query.isEmpty()) {
                String[] queryPairs = query.split("&");

                for (String queryPair : queryPairs) {
                    String[] keyValue = queryPair.split("=");

                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0], String.valueOf(StandardCharsets.UTF_8));
                        String value = URLDecoder.decode(keyValue[1], String.valueOf(StandardCharsets.UTF_8));
                        paramsMap.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("参数解析失败：" + e.getMessage());
        }

        return paramsMap;
    }
}
