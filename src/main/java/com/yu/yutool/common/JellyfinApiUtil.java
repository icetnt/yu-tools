package com.yu.yutool.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yu.yutool.exception.BaseException;
import com.yu.yutool.model.jf.JellyfinItem;
import com.yu.yutool.model.jf.JellyfinItemsResult;
import com.yu.yutool.model.jf.JellyfinUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JellyfinApiUtil
 */
@Slf4j
public class JellyfinApiUtil {

    public static void mediaRefresh() {
        if(StringUtils.isBlank(SysConfigParmas.JELLYFIN_URL) || StringUtils.isBlank(SysConfigParmas.JELLYFIN_API_TOKEN)) {
            throw new BaseException("请检查Jellyfin url/token参数配置!");
        }
        Map<String, String> header = new HashMap<>();
        header.put("X-Emby-Token", SysConfigParmas.JELLYFIN_API_TOKEN);

        String getUsersUrl = SysConfigParmas.JELLYFIN_URL + "/Users";
        String getUsersResult = HttpUtil.sendGet(getUsersUrl, header, null, null);
        List<JellyfinUser> jellyfinUsers = JSONArray.parseArray(getUsersResult, JellyfinUser.class);

        String userId = null;
        String userName = null;
        if(StringUtils.isNotBlank(SysConfigParmas.JELLYFIN_MEDIA_REFRESH_USER_NAME)) {
            for(JellyfinUser user : jellyfinUsers) {
                if(StringUtils.equals(user.getName(), SysConfigParmas.JELLYFIN_MEDIA_REFRESH_USER_NAME)) {
                    userId = user.getId();
                    userName = user.getName();
                    break;
                }
            }
        }else {
            for(JellyfinUser user : jellyfinUsers) {
                if(user.getPolicy().isIsAdministrator()) {
                    userId = user.getId();
                    userName = user.getName();
                    break;
                }
            }
        }
        if(StringUtils.isBlank(userId)) {
            throw new BaseException("用户获取失败");
        }
        log.info("开始基于用户'" + userName + "'刷新媒体库...");
        String getItemsUrl = SysConfigParmas.JELLYFIN_URL + "/Users/" + userId + "/Items";
        String getItemsResult = HttpUtil.sendGet(getItemsUrl, header, null, null);
        JellyfinItemsResult jellyfinItemsResult = JSON.parseObject(getItemsResult, JellyfinItemsResult.class);
        for (JellyfinItem item : jellyfinItemsResult.getItems()) {
            String refreshItemsUrl = SysConfigParmas.JELLYFIN_URL + "/Items/" + item.getId() + "/Refresh?metadataRefreshMode=Default&imageRefreshMode=Default&replaceAllMetadata=false&replaceAllImages=false";
            HttpUtil.sendPost(refreshItemsUrl, header, null, null);
            log.info("媒体库刷新成功: " + item.getName());
        }
    }


}
