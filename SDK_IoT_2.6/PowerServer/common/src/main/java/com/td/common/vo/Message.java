package com.td.common.vo;

import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;


/**
 * 客户端接口消息，所有的消息都必须继承自该类
 * 
 * @author chen
 * @version 1.0 2015-9-18
 */
@Data
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 *
	 *
	 *   msgId: 最长32位. 客户端类型小写(参考枚举ClientType)+年月日时分秒+5位随机数. e.g. android2016101811410012345.
	 *   timestamp: 时间戳yyyyMMddHHmmss, 用来验证请求时间偏差,时间偏差太大的请求会忽略. e.g. 20161018114100
	 *    messageType: 具体接口的名称, 区分大小写. e.g. ActivationDevice
	 *
	 */

	/**
	 * 消息编号
	 */
	private String msgId;

	/**
	 * 消息发送时间戳.e.g. yyyyMMddHHmmss
	 */
	private String timestamp;

	public Message(){
		super();
		setTimestamp(getTimesTamps());
		setMsgId(getMsgIds());
	}

	private String getTimesTamps(){
		return DateFormatUtils.format(Calendar.getInstance().getTime(), "yyyyMMddHHmmss");
	}

	private String getMsgIds(){
		return System.currentTimeMillis()+"0"+new Random().nextInt(10000);
	}



}
