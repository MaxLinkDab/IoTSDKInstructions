package com.td.common_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "alipay.miniapp",ignoreUnknownFields = true)
public class AliPayProperties {

    /**
     * 支付宝小程序appid
     */
    private String appid;

    /**
     * 支付宝秘钥
     */
    private String privateKey;

    /**
     * 支付宝公钥
     */
    private String publicKey;

    /**
     * 授权地址
     */
    private String serverUrl;

    /**
     * 支付宝支付的回调地址
     */
    private String notifyUrl;

}
