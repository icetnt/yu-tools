package com.yu.yutool.common;

/**
 * JellyfinConstants
 */
public class SysConfigParmas {

    public static String JELLYFIN_URL = null;
    public static String JELLYFIN_API_TOKEN = null;
    public static String JELLYFIN_MEDIA_REFRESH_USER_NAME = null;


    public static void init() {
        JELLYFIN_URL = System.getProperty("jellyfinUrl");
        JELLYFIN_API_TOKEN = System.getProperty("jellyfinApiToken");
        JELLYFIN_MEDIA_REFRESH_USER_NAME = System.getProperty("jellyfinMediaRefreshUserName");
    }

}
