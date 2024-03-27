package com.yu.yutool.controller;

import com.alibaba.fastjson.JSONObject;
import com.yu.yutool.common.CacheUtil;
import com.yu.yutool.common.QBApiUtil;
import com.yu.yutool.model.jf.JellyfinNoticeInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@Slf4j
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping("/mp")
    public String mp(@RequestBody JSONObject eventInfo) {
        if(null != eventInfo && StringUtils.equals(eventInfo.getString("type"), "download.added")) {
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) eventInfo.get("data");
            CacheUtil.DOWNLOADING_CACHE.add((String) data.get("hash"));
            log.info("MP开始下载...hash:" + data.get("hash"));
        }
        return "ok";
    }

    @PostMapping("/jf")
    public String jf(@RequestBody JellyfinNoticeInfo noticeInfo) {
        if(StringUtils.equals(noticeInfo.getNotificationType(), "PlaybackStart") || StringUtils.equals(noticeInfo.getNotificationType(), "PlaybackProgress")) {
            CacheUtil.JF_WATCHING_USER_CACHE.put(noticeInfo.getNotificationUsername(), 30);
            QBApiUtil.setUploadLimitByJfWatching();
        }
        return "ok";
    }

}
