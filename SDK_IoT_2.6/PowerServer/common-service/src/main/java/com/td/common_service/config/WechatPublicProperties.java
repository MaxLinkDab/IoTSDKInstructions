package com.td.common_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(
        prefix = "wechat",
        ignoreUnknownFields = true
)
public class WechatPublicProperties {

    private String appId;
    private String secret;
    /**
     * 自定义token 必须为英文或数字，长度为3-32字符。
     */
    private  String token;


}
