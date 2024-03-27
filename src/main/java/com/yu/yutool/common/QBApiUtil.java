package com.yu.yutool.common;

import com.yu.yutool.exception.BaseException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.params.CookiePolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * MoviePilotApiUtil
 */
@Slf4j
public class QBApiUtil {

    private static String ACCESS_TOKEN = "";

    public static String login() {
        String loginUrl = SysConfigParmas.QB_URL + "/api/v2/auth/login";
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(loginUrl);
        postMethod.addParameter("username", SysConfigParmas.QB_USERNAME);
        postMethod.addParameter("password", SysConfigParmas.QB_PASSWORD);
        try {
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            int statusCode = httpClient.executeMethod(postMethod);
            if (statusCode == 200) {
                String responseStr = postMethod.getResponseBodyAsString();
                if (StringUtils.isNotBlank(responseStr) && StringUtils.containsIgnoreCase(responseStr, "ok")) {
                    Cookie[] cookies = httpClient.getState().getCookies();
                    for (Cookie c : cookies) {
                        if(StringUtils.equalsIgnoreCase("SID", c.getName())) {
                            ACCESS_TOKEN = c.getValue();
                            log.info("QB登录成功!");
                            break;
                        }
                    }
                } else {
                    log.error("QB登录失败, resp:" + responseStr);
                }
            } else {
                log.error("QB登录失败, httpCode:" + statusCode);
            }
        } catch (Exception e) {
            throw new BaseException("QB登录失败：" + e.getMessage(), e);
        }
        return ACCESS_TOKEN;
    }

    /**
     * 查询上传限速 单位MB/s
     */
    public static String getUploadLimit() {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Cookie", "SID=" + ACCESS_TOKEN);
            String resp = HttpUtil.sendGet(SysConfigParmas.QB_URL + "/api/v2/transfer/uploadLimit", headers, null, "utf-8");
            if(StringUtils.equals(resp, "401") || StringUtils.equals(resp, "403")) {
                headers.put("Cookie", "SID=" + login());
                resp = HttpUtil.sendGet(SysConfigParmas.QB_URL + "/api/v2/transfer/uploadLimit", headers, null, "utf-8");
            }
            if(StringUtils.isNotBlank(resp) && StringUtils.isNumeric(resp)) {
                BigDecimal result = new BigDecimal(resp).divide(new BigDecimal(1024)).divide(new BigDecimal(1024), 2, RoundingMode.HALF_UP);
                return result.toString();
            }else {
                log.error("查询QB上传限速失败：" + resp);
                return null;
            }
        }catch (Exception e) {
            throw new BaseException("查询QB上传限速失败：" + e.getMessage(), e);
        }
    }

    /**
     * 设置上传限速 单位MB/s
     * 最大值102400
     * 最小值10
     */
    public static void setUploadLimit(String MBps) {
        try {
            double uploadLimitMBps = Double.parseDouble(MBps);
            if(uploadLimitMBps > 100) {
                uploadLimitMBps = 100d;
            }
            if(uploadLimitMBps < 0.01) {
                uploadLimitMBps = 0.01d;
            }
            Integer targetUploadLimitBps = Math.toIntExact(Math.round(uploadLimitMBps * 1024) * 1024);
            Map<String, String> headers = new HashMap<>();
            headers.put("Cookie", "SID=" + ACCESS_TOKEN);
            Map<String, String> params = new HashMap<>();
            params.put("limit", String.valueOf(targetUploadLimitBps));
            String resp = HttpUtil.sendPost(SysConfigParmas.QB_URL + "/api/v2/transfer/setUploadLimit", headers, params, "utf-8");
            if(StringUtils.equals(resp, "401") || StringUtils.equals(resp, "403")) {
                headers.put("Cookie", "SID=" + login());
                HttpUtil.sendPost(SysConfigParmas.QB_URL + "/api/v2/transfer/setUploadLimit", headers, params, "utf-8");
            }
            log.info("QB上传限速设置完成，限速：" + MBps + "MB/s");
        }catch (Exception e) {
            throw new BaseException("QB上传限速设置失败：" + e.getMessage(), e);
        }
   }

    /**
     * 查询并设置上传限速 单位MB/s
     * 如果目标已经一致则不设置
     */
    public static void setUploadLimitIfDif(String MBps) {
        String currentLimit = getUploadLimit();
        if(StringUtils.isBlank(currentLimit) || new BigDecimal(MBps).compareTo(new BigDecimal(getUploadLimit())) != 0) {
            setUploadLimit(MBps);
        }
    }

    @Synchronized
    public static void setUploadLimitByJfWatching() {
        double upLimitMBps = Math.max(Double.parseDouble(SysConfigParmas.QB_UP_LIMIT_MIN),
                Double.parseDouble(SysConfigParmas.QB_UP_LIMIT_MAX) - (CacheUtil.JF_WATCHING_USER_CACHE.size() * SysConfigParmas.QB_UP_LIMIT_PER_USER));
        setUploadLimitIfDif(String.valueOf(upLimitMBps));
    }

}
