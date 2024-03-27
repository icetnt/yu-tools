package com.yu.yutool.model.jf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JellyfinNoticeInfo
 */
@Data
public class JellyfinNoticeInfo {

    /**
     * 通知类型（PlaybackStart、PlaybackStop、PlaybackProgress）
     */
    @JsonProperty("NotificationType")
    private String NotificationType;

    /**
     * 观看用户名
     */
    @JsonProperty("NotificationUsername")
    private String NotificationUsername;

    /**
     * 剧集类型（Movie、Episode）
     */
    @JsonProperty("ItemType")
    private String ItemType;

    /**
     * 电视剧名称
     */
    @JsonProperty("SeriesName")
    private String SeriesName;

    /**
     * 电视剧本集名称/电影名称
     */
    @JsonProperty("Name")
    private String Name;

    /**
     * 客户端名称
     */
    @JsonProperty("ClientName")
    private String ClientName;

    /**
     * 设备名称
     */
    @JsonProperty("DeviceName")
    private String DeviceName;



}
