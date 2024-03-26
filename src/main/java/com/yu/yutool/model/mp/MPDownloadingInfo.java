package com.yu.yutool.model.mp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MPDownloadingInfo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MPDownloadingInfo {

    private String hash;
    private String title;
    private String name;
    private String year;
    private String season_episode;
    private Double size;
    private Double progress;
    private String state;
    private String upspeed;
    private String dlspeed;
    private MediaInfo media;
    private String userid;
    private String username;
    private String left_time;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class MediaInfo {
        private String tmdbid;
        private String type;
        private String title;
        private String season;
        private String episode;
        private String image;
    }

}
