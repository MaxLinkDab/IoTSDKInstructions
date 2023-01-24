package com.td.util.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 
 * @author Administrator
 *
 */
@Data
@ConfigurationProperties(prefix = "device.eject")
@Configuration
public class DeviceEjectConfig {

	public static int SLEEP;
	public static String REDIS_KEY;
	public static int EXPIRE_TIME;
	public static int CHARGE_COUNT;
	public static int MAX_CHARGE_TIME;
	public static int MIN_CHARGE_TIME;

	public void setSleep(int sleep) {
		SLEEP = sleep;
	}
	public int getSleep() {
		return SLEEP;
	}
	public String getRedisKey() {
		return REDIS_KEY;
	}
	public void setRedisKey(String redisKey) {
		REDIS_KEY = redisKey;
	}
	public void setExpireTime(int expireTime) {
		EXPIRE_TIME = expireTime;
	}
	public int getExpireTime() {
		return EXPIRE_TIME;
	}

	public int getChargeCount() {
		return CHARGE_COUNT;
	}

	public void setChargeCount(int chargeCount) {
		CHARGE_COUNT = chargeCount;
	}

	public int getMaxChargeTime() {
		return MAX_CHARGE_TIME;
	}

	public void setMaxChargeTime(int maxChargeTime) {
		MAX_CHARGE_TIME = maxChargeTime;
	}

	public int getMinChargeTime() {
		return MIN_CHARGE_TIME;
	}

	public void setMinChargeTime(int minChargeTime) {
		MIN_CHARGE_TIME = minChargeTime;
	}
}
