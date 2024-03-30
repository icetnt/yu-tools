package com.yu.yutool.model.qb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QBTorrentDetail
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QBTorrentDetail {

    private String name;
    private String hash;
    private Long eta;
    private Long completion_date;
    private Long total_size;

}


