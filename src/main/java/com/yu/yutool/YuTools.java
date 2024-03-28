package com.yu.yutool;

import com.yu.yutool.common.SysConfigParmas;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class YuTools {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(YuTools.class);
//        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
        log.info("====================YU-TOOLS SERVICE STARTED!====================");
        SysConfigParmas.init();
    }
}
