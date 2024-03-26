package com.yu.yutool.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yu.yutool.exception.BaseException;
import com.yu.yutool.model.mp.MPDownloadingInfo;
import com.yu.yutool.model.mp.MPLoginResp;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MoviePilotApiUtil
 */
public class MoviePilotApiUtil {

    private static String ACCESS_TOKEN = "";

    public static String getNewToken() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", SysConfigParmas.MOVIE_PILOT_USERNAME);
        parameters.put("password", SysConfigParmas.MOVIE_PILOT_PASSWORD);
        try {
            String resp = HttpUtil.sendPost(SysConfigParmas.MOVIE_PILOT_URL + "/api/v1/login/access-token", null, parameters, "utf-8");
            MPLoginResp loginResp = JSON.parseObject(resp, MPLoginResp.class);
            ACCESS_TOKEN = loginResp.getAccess_token();
            return ACCESS_TOKEN;
        }catch (Exception e) {
            throw new BaseException("MP登录失败：" + e.getMessage(), e);
        }
    }

    public static List<MPDownloadingInfo> getDownloadingList() {
        List<MPDownloadingInfo> result;
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + ACCESS_TOKEN);
            String resp = HttpUtil.sendGet(SysConfigParmas.MOVIE_PILOT_URL + "/api/v1/download/", headers, null, "utf-8");
            if(StringUtils.equals(resp, "401") || StringUtils.equals(resp, "403")) {
                headers.put("Authorization", "Bearer " + getNewToken());
                resp = HttpUtil.sendGet(SysConfigParmas.MOVIE_PILOT_URL + "/api/v1/download/", headers, null, "utf-8");
            }
            result = JSONArray.parseArray(resp, MPDownloadingInfo.class);
        }catch (Exception e) {
            throw new BaseException("获取下载列表失败：" + e.getMessage(), e);
        }
        return result;
   }

}
