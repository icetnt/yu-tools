package com.yu.yutool.model;

import lombok.Data;

import java.util.List;

/**
 * JellyfinItemsResult
 */
@Data
public class JellyfinItemsResult {

    private List<JellyfinItem> Items;
    private Integer TotalRecordCount;
    private Integer StartIndex;

}
