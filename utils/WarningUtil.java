package com.yiyouliao.autoprod.liaoyuan.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.yiyouliao.autoprod.common.constant.Env;
import com.yiyouliao.autoprod.common.enums.PhoneEnums;
import com.yiyouliao.autoprod.liaoyuan.constants.RedisKeyConstant;
import com.yiyouliao.autoprod.liaoyuan.lua.RedisManager;
import com.yiyouliao.autoprod.liaoyuan.mq.data.ReteLimitWarnMessage;
import com.yiyouliao.autoprod.liaoyuan.mq.producer.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xieDong
 * @date 2023/4/21
 * @apiNote
 */
@Slf4j
@Component
public class WarningUtil {

    @Value("${application.env}")
    private String env;

    private static String staticEnv;

    @Value("${third-party.weixin.wechat-url}")
    private String wechatUrl;

    private static String staticWechatUrl;

    @Resource
    private RedisManager redisManager;

    private static RedisManager redisManagerStatic;

    @Value("${mq.rete.limit.exchange}")
    private String exchange;

    @Value("${mq.rete.limit.routing}")
    private String routingKey;

    private static String exchangeStatic;

    private static String routingKeyStatic;

    @Resource
    private MessageProducer messageProducer;

    private static MessageProducer messageProducerStatic;

    @PostConstruct
    public void init() {
        staticEnv = env;
        staticWechatUrl = wechatUrl;
        redisManagerStatic = redisManager;
        messageProducerStatic = messageProducer;
        exchangeStatic = exchange;
        routingKeyStatic = routingKey;
    }

    /**
     * 向企业微信发送报警信息
     *
     * @param functionName  告警项
     * @param remindContent 告警内容
     */
    public static void sendWarningToQW(String functionName, String remindContent, PhoneEnums... enums) {
        //生产环境才进行报警
        if (!"online".equals(staticEnv)) {
            return;
        }
        //获取令牌桶令牌
        boolean isAllow = redisManagerStatic.rateLimit(RedisKeyConstant.RATE_LIMIT_KEY + "warn", 20, 60000);
        //如果获取不到则发送到限流处理队列中
        if (!isAllow) {
            ReteLimitWarnMessage reteLimitWarnMessage = new ReteLimitWarnMessage();
            reteLimitWarnMessage.setFunctionName(functionName);
            reteLimitWarnMessage.setRemindContent(remindContent);
            reteLimitWarnMessage.setEnums(enums);
            Message message = MessageBuilder.withBody(JSON.toJSONString(reteLimitWarnMessage).getBytes(StandardCharsets.UTF_8)).build();
            messageProducerStatic.sendWithCallback(message, exchangeStatic, routingKeyStatic);
            return;
        }

        sendWarning(functionName, remindContent, enums);
    }

    public static void sendWarning(String functionName, String remindContent, PhoneEnums... enums) {
        log.info("触发企业微信告警，当前告警环境：{},告警url:{}", staticEnv, staticWechatUrl);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        String environment = Env.match(staticEnv);
        JSONObject param = JSONUtil.createObj();
        Map<String, Object> textObject = new HashMap<>(4);
        textObject.put("content", "【" + format + "】" + "\n" + "【" + environment + "】"  + "\n" + "【告警项:" + functionName+"】\n" + remindContent);
        textObject.put("mentioned_mobile_list", PhoneEnums.getPhoneList(enums));
        param.set("msgtype", "text");
        param.set("text", textObject);
        HttpUtil.createPost(staticWechatUrl)
                .body(param.toString())
                .timeout(10000)
                .execute();
    }

}
