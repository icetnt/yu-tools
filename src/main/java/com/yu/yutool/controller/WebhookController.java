package com.yu.yutool.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.yu.yutool.common.CacheUtil;
import com.yu.yutool.common.QBApiUtil;
import com.yu.yutool.model.jf.JellyfinNoticeInfo;
import com.yu.yutool.model.mp.MPEventInfo;
import com.yu.yutool.model.mp.MPDownloadAddInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @PostMapping("/mp")
    public String mp(@RequestBody String webhookData) {
        try {
            MPEventInfo eventInfo = JSONObject.parseObject(webhookData, MPEventInfo.class);
            if(null != eventInfo && StringUtils.equals(eventInfo.getType(), "download.added")) {
                MPDownloadAddInfo mpDownloadAddInfo = JSON.parseObject(JSON.toJSONString(eventInfo.getData()), MPDownloadAddInfo.class);
                CacheUtil.DOWNLOADING_CACHE.put(mpDownloadAddInfo.getHash(), mpDownloadAddInfo);
                log.info("MP开始下载...下载内容:{}, hash:{}", mpDownloadAddInfo.getTitleName(), mpDownloadAddInfo.getHash());
            }
        }catch (Exception e) {
            log.error("MP回调数据解析异常, webhookData:{}, errorMsg:{}", webhookData, e.getMessage());
        }
        return "ok";
    }

    @PostMapping("/jf")
    public String jf(@RequestBody String webhookData) {
        try {
            JellyfinNoticeInfo noticeInfo = JSON.parseObject(webhookData, JellyfinNoticeInfo.class);
            if(StringUtils.equals(noticeInfo.getNotificationType(), "PlaybackStart") || StringUtils.equals(noticeInfo.getNotificationType(), "PlaybackProgress")) {
                if(!StringUtils.containsIgnoreCase(noticeInfo.getDeviceName(), "internal")) {
                    String watchingKey = noticeInfo.getNotificationUsername() + ":" + noticeInfo.getDeviceName() + ":" + noticeInfo.getClientName();
                    if(CacheUtil.JF_WATCHING_USER_CACHE.containsKey(watchingKey)) {
                        //更新观看者
                        CacheUtil.JF_WATCHING_USER_CACHE.put(watchingKey, 30);
                    }else {
                        //新增观看者
                        String mediaName = StringUtils.equalsIgnoreCase(noticeInfo.getItemType(), "movie") ? noticeInfo.getName() : noticeInfo.getSeriesName() + "-" + noticeInfo.getName();
                        log.info(String.format("开始观看...用户:%s; 设备:%s; 客户端:%s; 观看内容:%s",
                                noticeInfo.getNotificationUsername(), noticeInfo.getDeviceName(), noticeInfo.getClientName(), mediaName));
                        CacheUtil.JF_WATCHING_USER_CACHE.put(watchingKey, 30);
                        QBApiUtil.setUploadLimitByJfWatching();
                    }
                }
            }
        }catch (Exception e) {
            log.error("JF回调数据解析异常, webhookData:{}, errorMsg:{}", webhookData, e.getMessage());
        }
        return "ok";
    }

}
