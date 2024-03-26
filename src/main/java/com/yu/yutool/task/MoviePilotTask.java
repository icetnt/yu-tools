package com.yu.yutool.task;

import com.yu.yutool.common.CacheUtil;
import com.yu.yutool.common.JellyfinApiUtil;
import com.yu.yutool.common.MoviePilotApiUtil;
import com.yu.yutool.model.mp.MPDownloadingInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MoviePilotTask
 */
@Slf4j
@Service
public class MoviePilotTask {

    @Scheduled(fixedRate = 10000) // 每10秒执行一次
    public void downloadingCheck() {
        if(!CacheUtil.DOWNLOADING_CACHE.isEmpty()) {
            List<MPDownloadingInfo> downloadingList = MoviePilotApiUtil.getDownloadingList();
            Map<String, MPDownloadingInfo> downloadingMap = downloadingList.stream().collect(Collectors.toMap(MPDownloadingInfo::getHash, dlInfo -> dlInfo));
            for (String hash : new ArrayList<>(CacheUtil.DOWNLOADING_CACHE)) {
                if(!downloadingMap.containsKey(hash)) {
                    CacheUtil.DOWNLOADING_CACHE.remove(hash);
                    log.info("===================== MP download ok, media refresh start =====================");
                    JellyfinApiUtil.mediaRefresh();
                    log.info("===================== MP download ok, media refresh done ======================");
                }
            }
        }
    }

}
