package com.yu.yutool.common;

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


    public static void init() {
        JELLYFIN_URL = System.getProperty("jellyfinUrl");
        JELLYFIN_API_TOKEN = System.getProperty("jellyfinApiToken");
        JELLYFIN_MEDIA_REFRESH_USER_NAME = System.getProperty("jellyfinMediaRefreshUserName");
        MOVIE_PILOT_URL = System.getProperty("moviePilotUrl");
        MOVIE_PILOT_USERNAME = System.getProperty("moviePilotUsername");
        MOVIE_PILOT_PASSWORD = System.getProperty("moviePilotPassword");
    }

}
