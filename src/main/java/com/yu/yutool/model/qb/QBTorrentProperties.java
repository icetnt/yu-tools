package com.yu.yutool.model.qb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QBTorrentProperties
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QBTorrentProperties {

    private String name;
    private String hash;
    private Integer completion_date;
    private String save_path;

}
