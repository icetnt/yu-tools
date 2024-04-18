package com.yu.yutool.task;

import cn.hutool.core.date.DateUtil;
import com.yu.yutool.common.*;
import com.yu.yutool.model.mp.MPDownloadAddInfo;
import com.yu.yutool.model.qb.QBTorrentDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;

/**
 * MoviePilotTask
 */
@Slf4j
@Service
public class MoviePilotTask {

    @Scheduled(fixedRate = 10000) // 每10秒执行一次
    public void downloadingCheck() {
        if(!CacheUtil.DOWNLOADING_CACHE.isEmpty()) {
            for(String hash : new HashSet<>(CacheUtil.DOWNLOADING_CACHE.keySet())) {
                QBTorrentDetail qbTorrentDetail = QBApiUtil.getDetail(hash);
                MPDownloadAddInfo mpDownloadAddInfo = CacheUtil.DOWNLOADING_CACHE.get(hash);
                if(null == qbTorrentDetail) {
                    CacheUtil.DOWNLOADING_CACHE.remove(hash);
                    if(null != mpDownloadAddInfo) {
                        log.info("下载已删除...下载内容:{}, hash:{}", mpDownloadAddInfo.getTitleName(), mpDownloadAddInfo.getHash());
                        TgApiUtil.sendPhoto(SysConfigParmas.TG_BOT_TOKEN, SysConfigParmas.TG_CHAT_ID, mpDownloadAddInfo.getImgUrl(), mpDownloadAddInfo.getMdMsg("下载删除", "已删除"));
                    }
                }else if(qbTorrentDetail.getCompletion_date() > 0) {
                    CacheUtil.DOWNLOADING_CACHE.remove(hash);
                    if(null != mpDownloadAddInfo) {
                        log.info("下载已完成...下载内容:{}, hash:{}", mpDownloadAddInfo.getTitleName(), mpDownloadAddInfo.getHash());
                        TgApiUtil.sendPhoto(SysConfigParmas.TG_BOT_TOKEN, SysConfigParmas.TG_CHAT_ID, mpDownloadAddInfo.getImgUrl(),
                                mpDownloadAddInfo.getMdMsg("下载完成", DateUtil.format(new Date(qbTorrentDetail.getCompletion_date() * 1000), "yyyy-MM-dd HH:mm:ss")));
                    }
                    log.info("===================== media download ok, refresh start =====================");
                    JellyfinApiUtil.mediaRefresh();
                    log.info("===================== media download ok, refresh done ======================");
                }
            }
        }
    }

}
