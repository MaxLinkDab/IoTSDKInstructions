package com.td.util.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//import lombok.Data;

/**
 * 服务端API签名配置文件
 */
//@Data
@Component
@ConfigurationProperties(prefix = "aliyun.iot")
public class AliyunConfig {

	// private final static String CHARSET_UTF8 = "utf8";

//	private String accessKeyId;
//	private String accessKeySecret;
//	private String regionId;
//	private String productCode;
//	private String domain;
//	private String version;
//	private String uid;
//	private String productKey;

	public static String ACCESS_KEY_ID = "LTAI5tLg1fSDbppNRKR3Znzr";
	public static String ACCESS_KEY_SECRET = "eK9SoAT4MuDM5UynmTDywxLZ12pL6u";
	public static String REGION_ID = "cn-shanghai";
	public static String PRODUCT_CODE = "Iot";
	public static String DOMAIN = "cn-shanghai.aliyuncs.com";
	public static String VERSION = "2018-01-20";
	public static String UID = "862506049385829";
	public static String PRODUCT_KEY = "a1fe2TxOa3N";
	public static String CONSUMER_GROUP_ID = "DEFAULT_GROUP";
	public static String CLIENT_ID = "client1";

	public String getConsumerGroupId() {
		return CONSUMER_GROUP_ID;
	}

	public void setConsumerGroupId(String consumerGroupId) {
		CONSUMER_GROUP_ID = consumerGroupId;
	}

	public String getClientId() {
		return CLIENT_ID;
	}

	public void setClientId(String clientId) {
		CLIENT_ID = clientId;
	}
	public String getAccessKeyId() {
		return ACCESS_KEY_ID;
	}
	public void setAccessKeyId(String accessKeyId) {
		ACCESS_KEY_ID = accessKeyId;
	}
	public String getAccessKeySecret() {
		return ACCESS_KEY_SECRET;
	}
	public void setAccessKeySecret(String accessKeySecret) {
		ACCESS_KEY_SECRET = accessKeySecret;
	}
	public String getRegionId() {
		return REGION_ID;
	}
	public void setRegionId(String regionId) {
		REGION_ID = regionId;
	}
	public String getProductCode() {
		return PRODUCT_CODE;
	}
	public void setProductCode(String productCode) {
		PRODUCT_CODE = productCode;
	}
	public String getDomain() {
		return DOMAIN;
	}
	public void setDomain(String domain) {
		DOMAIN = domain;
	}
	public String getVersion() {
		return VERSION;
	}
	public void setVersion(String version) {
		VERSION = version;
	}
	public String getUid() {
		return UID;
	}
	public void setUid(String uid) {
		UID = uid;
	}
	public String getProductKey() {
		return PRODUCT_KEY;
	}
	public void setProductKey(String productKey) {
		PRODUCT_KEY = productKey;
	}
	
}
