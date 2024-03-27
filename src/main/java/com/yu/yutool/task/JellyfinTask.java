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
            for(String key : new HashSet<>(CacheUtil.JF_WATCHING_USER_CACHE.keySet())) {
                CacheUtil.JF_WATCHING_USER_CACHE.put(key, CacheUtil.JF_WATCHING_USER_CACHE.get(key) - 10);
                if(CacheUtil.JF_WATCHING_USER_CACHE.get(key) <= 0) {
                    CacheUtil.JF_WATCHING_USER_CACHE.remove(key);
                }
            }
            QBApiUtil.setUploadLimitByJfWatching();
        }
    }

}
