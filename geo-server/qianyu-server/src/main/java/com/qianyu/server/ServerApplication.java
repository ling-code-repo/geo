package com.qianyu.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类、
 *
 * @author qianyu
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${yudao.info.base-package}
@SpringBootApplication(scanBasePackages = {"${yudao.info.base-package}.server", "${yudao.info.base-package}.module"})
public class ServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(ServerApplication.class, args);
    }

}
