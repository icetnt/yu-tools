package com.yu.yutool.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class TestController {

    @GetMapping("/world")
    public String hello(String param) {
        return "Hello World! " + (StringUtils.isNotBlank(param) ? param : "");
    }

}
