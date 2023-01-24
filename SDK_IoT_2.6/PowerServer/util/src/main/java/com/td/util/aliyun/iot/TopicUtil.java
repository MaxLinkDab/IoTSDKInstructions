package com.td.util.aliyun.iot;

/**
 * 
 * @author Administrator
 *
 */
public /* static */final class TopicUtil {

	/**
	 * _先只针对这种格式的， 正则后续。
	 * @param topic
	 * @return
	 */
	public static final String getDeviceNO(String topic) {
		String str_devNO = topic.substring(topic.indexOf('/', 1) + 1, topic.lastIndexOf('/'));
		return str_devNO;
	}

}
