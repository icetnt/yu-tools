package com.yu.yutool;

import com.yu.yutool.common.SysConfigParmas;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@SpringBootApplication
public class YuToolsApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuToolsApplication.class, args);
        log.info("====================YU-TOOLS SERVICE STARTED!====================");
        SysConfigParmas.init();
    }
}
