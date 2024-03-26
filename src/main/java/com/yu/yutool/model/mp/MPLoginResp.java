package com.yu.yutool.model.mp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MPLoginResp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MPLoginResp {

    private String access_token;
    private String token_type;
    private String super_user;
    private String user_name;
    private String avatar;

}
