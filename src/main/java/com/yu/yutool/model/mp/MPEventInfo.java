package com.yu.yutool.model.mp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MPEventInfo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MPEventInfo {

    private String type;
    private Object data;

}
