package com.yu.yutool.common;

import com.yu.yutool.model.mp.MPDownloadAddInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * CacheUtil
 */
public class CacheUtil {

    public static final Map<String, MPDownloadAddInfo> DOWNLOADING_CACHE = new HashMap<>();

    public static final Map<String, Integer> JF_WATCHING_USER_CACHE = new HashMap<>();

}
