package com.yu.yutool.model.jf;

import lombok.Data;

/**
 * JellyfinUser
 */
@Data
public class JellyfinUser {

    private String Name;
    private String ServerId;
    private String Id;
    private JellyfinUserPolicy Policy;

    @Data
    public static class JellyfinUserPolicy {
        private boolean IsAdministrator;
        private boolean IsDisabled;
    }

}
