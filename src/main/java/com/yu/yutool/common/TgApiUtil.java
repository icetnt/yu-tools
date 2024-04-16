package com.yu.yutool.common;

import com.yu.yutool.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * TgApiUtil
 */
@Slf4j
public class TgApiUtil {

    /**
     * 发送图片消息
     */
    public static void sendPhoto(String botApiToken, String chatId, String photoUrl, String caption) {
        if(StringUtils.isBlank(botApiToken) || StringUtils.isBlank(chatId)) {
            log.info("TG send msg cancel... botToken or chatId is null.");
        }
        try {
            Map<String, String> jsonParams = new HashMap<>();
            jsonParams.put("chat_id", chatId);
            jsonParams.put("photo", StringUtils.replace(photoUrl, "original", "w500"));
            jsonParams.put("parse_mode", "Markdown");
            jsonParams.put("caption", caption);
            HttpUtil.sendPost(String.format("https://api.telegram.org/bot%s/sendPhoto", botApiToken), null, jsonParams, "utf-8");
            log.info("TG send msg success.");
        }catch (Exception e) {
            throw new BaseException("TG发送图片消息失败：" + e.getMessage(), e);
        }
   }

}
