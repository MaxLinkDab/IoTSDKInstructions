package com.td.util.aliyun.iot;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.td.util.config.AliyunConfig;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyuncs.iot.model.v20180120.PubRequest;
import com.aliyuncs.iot.model.v20180120.PubResponse;
import com.td.util.aliyun.iot.base.AbstractAliyunClient;

public class AliyunClientUtil extends AbstractAliyunClient {

	private static final Logger LOG = LoggerFactory.getLogger(AliyunClientUtil.class);

	/**
	 * pub消息
	 *
	 * @param productKey pk
	 * @param topic      topic
	 * @param msg        消息内容

	public static boolean pub(String productKey, String topic, String msg) {
		boolean flag = false;

		PubRequest request = new PubRequest();
		request.setProductKey(productKey);
		request.setTopicFullName(topic);
		request.setMessageContent(Base64.encodeBase64String(msg.getBytes()));
		request.setQos(0);
		PubResponse response = (PubResponse) executeTest(request);
		if (response != null && response.getSuccess() != false) {
			flag = true;
			LOG.info("发送消息成功！messageId：" + response.getMessageId());
		} else {
			flag = false;
			LOG.info("发送消息失败！requestId:" + response.getRequestId() + "原因：" + response.getErrorMessage());
		}

		return flag;
	}*/

	/**
	 * pub消息
	 *
	 * @param productKey pk
	 * @param topic      topic
	 * @param msg        消息内容
	 */
	public static boolean pub(String productKey, String topic, String msg) {
		boolean flag = false;
		//设置client的参数
		DefaultProfile profile = null;
		try {
			profile = DefaultProfile.getProfile(AliyunConfig.REGION_ID, AliyunConfig.ACCESS_KEY_ID, AliyunConfig.ACCESS_KEY_SECRET);
		} catch (Exception e) {
			e.printStackTrace();
		}
		IAcsClient client = new DefaultAcsClient(profile);
		PubRequest request = new PubRequest();
		request.setProductKey(productKey);
		request.setTopicFullName(topic);
		request.setMessageContent(Base64.encodeBase64String(msg.getBytes()));
		request.setQos(0);
		PubResponse response = null;
		try {
			response = client.getAcsResponse(request);
		} catch (ClientException e) {
			e.printStackTrace();
		}
		if (response != null && response.getSuccess() != false) {
			flag = true;
			LOG.info("发送消息成功！messageId：" + response.getMessageId());
		} else {
			flag = false;
			LOG.info("发送消息失败！requestId:" + response.getRequestId() + "原因：" + response.getErrorMessage());
		}

		return flag;
	}

}
