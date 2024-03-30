package com.yu.yutool.model.mp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

/**
 * MPDownloadAddInfo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MPDownloadAddInfo {
    private String hash;
    private Context context;

    @Data
    public class Context {
        private MetaInfo meta_info;
        private TorrentInfo torrent_info;
        private MediaInfo media_info;
    }

    @Data
    public class MetaInfo {
        private String name;
        private String title;
        private String subtitle;
        private String type;
        private String season_episode;
        private String image;
    }

    @Data
    public class TorrentInfo {
        private String title;
        private String site_name;
        private String description;
        private Long size;
    }

    @Data
    public class MediaInfo {
        private String title;
        private String title_year;
        private String type;
        private String backdrop_path;
        private String poster_path;
    }

    public String getTitleName() {
        if(null == context) {
            return null;
        }
        StringBuilder titleName = new StringBuilder();
        if(null != context.media_info) {
            titleName.append(StringUtils.isNotBlank(context.media_info.title_year) ? context.media_info.title_year : context.media_info.title);
            titleName.append(" ");
        }else if(null != context.meta_info) {
            titleName.append(StringUtils.isNotBlank(context.meta_info.name) ? context.meta_info.name : context.meta_info.title);
            titleName.append(" ");
        }
        if(null != context.meta_info && StringUtils.isNotBlank(context.meta_info.season_episode)) {
            titleName.append(context.meta_info.season_episode);
        }
        return titleName.toString();
    }

    public String getImgUrl() {
        if(null == context || context.media_info == null) {
            return null;
        }
        return StringUtils.isNotBlank(context.media_info.backdrop_path) ? context.media_info.backdrop_path : context.media_info.poster_path;
    }

    public String getSiteName() {
        if(null == context || null == context.torrent_info) {
            return null;
        }
        return context.torrent_info.site_name;
    }

    public String getTorrentTile() {
        if(null == context || null == context.torrent_info) {
            return null;
        }
        return context.torrent_info.title;
    }

    public String getDescription() {
        if(null == context || null == context.torrent_info) {
            return null;
        }
        return context.torrent_info.description;
    }

    public String getSize() {
        if(null == context || null == context.torrent_info) {
            return null;
        }
        return readableFileSize(context.torrent_info.size);
    }

    public String getMdMsg(String customMsg, String finishTimeStr) {
        return "*" + getTitleName() + " " + customMsg + "*" +
                "\n站点：" + getSiteName() +
                "\n大小：" + getSize() +
                "\n完成时间：" + finishTimeStr;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
    }

}
