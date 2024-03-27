package com.yu.yutool.common;

import org.apache.commons.lang3.StringUtils;

/**
 * JellyfinConstants
 */
public class SysConfigParmas {

    public static String JELLYFIN_URL = null;
    public static String JELLYFIN_API_TOKEN = null;
    public static String JELLYFIN_MEDIA_REFRESH_USER_NAME = null;
    public static String MOVIE_PILOT_URL = null;
    public static String MOVIE_PILOT_USERNAME = null;
    public static String MOVIE_PILOT_PASSWORD = null;
    public static String QB_URL = null;
    public static String QB_USERNAME = null;
    public static String QB_PASSWORD = null;
    //单用户占用上传带宽（单位MB/s）
    public static Integer QB_UP_LIMIT_PER_USER = 2;
    //上传限速最大值（单位MB/s）
    public static String QB_UP_LIMIT_MAX = "11";
    //上传限速最小值（单位MB/s）
    public static String QB_UP_LIMIT_MIN = "0.5";


    public static void init() {
        JELLYFIN_URL = System.getProperty("jfUrl");
        JELLYFIN_API_TOKEN = System.getProperty("jfToken");
        JELLYFIN_MEDIA_REFRESH_USER_NAME = System.getProperty("jfRefreshUser");
        MOVIE_PILOT_URL = System.getProperty("mpUrl");
        MOVIE_PILOT_USERNAME = System.getProperty("mpUser");
        MOVIE_PILOT_PASSWORD = System.getProperty("mpPwd");
        QB_URL = System.getProperty("qbUrl");
        QB_USERNAME = System.getProperty("qbUser");
        QB_PASSWORD = System.getProperty("qbPwd");
        String qbUMPU = System.getProperty("qbUMPU");
        QB_UP_LIMIT_PER_USER = StringUtils.isBlank(qbUMPU) ? 2 : Integer.parseInt(qbUMPU);
        String qbUMax = System.getProperty("qbUMax");
        QB_UP_LIMIT_MAX = StringUtils.isBlank(qbUMax) ? "11" : qbUMax;
        String qbUMin = System.getProperty("qbUMin");
        QB_UP_LIMIT_MIN = StringUtils.isBlank(qbUMin) ? "0.5" : qbUMin;
    }

}
