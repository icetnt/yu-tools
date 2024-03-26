package com.yu.yutool.controller;

import com.alibaba.fastjson.JSONObject;
import com.yu.yutool.common.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

@Slf4j
@RestController
@RequestMapping("/movie-pilot")
public class MoviePilotController {

    @PostMapping("/webhook")
    public String mediaRefresh(@RequestBody JSONObject eventInfo) {
        if(eventInfo.getString("type").equals("download.added")) {
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) eventInfo.get("data");
            CacheUtil.DOWNLOADING_CACHE.add((String) data.get("hash"));
        }
        return "ok";
    }

}
