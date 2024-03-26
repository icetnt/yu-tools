package com.yu.yutool.controller;

import com.yu.yutool.common.JellyfinApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/jellyfin")
public class JellyfinController {

    @GetMapping("/media/refresh")
    public String mediaRefresh() {
        log.info("=========================== API media refresh start ===========================");
        JellyfinApiUtil.mediaRefresh();
        log.info("=========================== API media refresh done ============================");
        return "ok";
    }

}
