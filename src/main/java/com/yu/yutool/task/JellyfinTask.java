package com.yu.yutool.task;

import com.yu.yutool.common.CacheUtil;
import com.yu.yutool.common.QBApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * JellyfinTask
 */
@Slf4j
@Service
public class JellyfinTask {

    @Scheduled(fixedRate = 10000) // 每10秒执行一次
    public void jellyfinWatchingCheck() {
        if(!CacheUtil.JF_WATCHING_USER_CACHE.isEmpty()) {
            boolean removed = false;
            for(String key : new HashSet<>(CacheUtil.JF_WATCHING_USER_CACHE.keySet())) {
                CacheUtil.JF_WATCHING_USER_CACHE.put(key, CacheUtil.JF_WATCHING_USER_CACHE.get(key) - 10);
                if(CacheUtil.JF_WATCHING_USER_CACHE.get(key) <= 0) {
                    String[] keyArr = key.split(":");
                    if(keyArr.length == 3) {
                        log.info(String.format("结束观看...用户:%s; 设备:%s; 客户端:%s", keyArr[0], keyArr[1], keyArr[2]));
                    }
                    CacheUtil.JF_WATCHING_USER_CACHE.remove(key);
                    removed = true;
                }
            }
            if(removed) {
                QBApiUtil.setUploadLimitByJfWatching();
            }
        }
    }

}
