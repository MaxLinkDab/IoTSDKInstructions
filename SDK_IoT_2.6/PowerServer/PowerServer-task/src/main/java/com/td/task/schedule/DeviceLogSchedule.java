//package com.td.task.schedule;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.aliyun.openservices.iot.api.Profile;
//import com.aliyun.openservices.iot.api.message.MessageClientFactory;
//import com.aliyun.openservices.iot.api.message.api.MessageClient;
//import com.aliyun.openservices.iot.api.message.callback.MessageCallback;
//import com.aliyun.openservices.iot.api.message.callback.MessageCallback.Action;
//import com.aliyun.openservices.iot.api.message.entity.Message;
//import com.aliyun.openservices.iot.api.message.entity.MessageToken;
//import com.td.common.constant.IDeviceConstant;
//import com.td.common.enums.DeviceLogTypeEnum;
//import com.td.common_service.model.DeviceInfo;
//import com.td.common_service.service.DeviceService;
//import com.td.task.model.DeviceLog;
//import com.td.task.service.IDeviceLogService;
//import com.td.util.UtilTool;
//import com.td.util.aliyun.iot.AliyunClientUtil;
//import com.td.util.config.AliyunConfig;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.Date;
//
//@Component
//@Slf4j
//public class DeviceLogSchedule {
//
//	private static final Logger LOG = LoggerFactory.getLogger(DeviceLogSchedule.class);
//
//	@Autowired
//	private AliyunConfig aliyunConfig;
//
//	@Autowired
//	private IDeviceLogService deviceLogService;
//	@Autowired
//	private DeviceService deviceService;
//
//	/**
//	 * _自定义Topic 设备归还等操作
//	 * @param messageToken
//	 * @return
//	 */
//	private Action consumeCustom(final MessageToken messageToken) {
//		Message msg = messageToken.getMessage();
//		String str_msg = new String(msg.getPayload());
//		LOG.debug("自定义Topic msg=" + msg);
//
//
//		if (str_msg != null && str_msg.length() > 4) {
//			String str_prefix = str_msg.substring(0, 3);
//			if (	str_prefix.equals(IDeviceConstant.RETURN_COMMAND_START) ||
//					str_prefix.equals(IDeviceConstant.DTU_COMMAND_START) ||
//					str_prefix.equals(IDeviceConstant.INFO_UPLOAD_COMMAND_START) ||
//					str_prefix.equals(IDeviceConstant.P2P_COMMAND_START) ||
//					str_prefix.equals(IDeviceConstant.CHECK_TIME) ||
//					str_prefix.equals(IDeviceConstant.DTU_UPDATE_START) ||
//					str_prefix.equals(IDeviceConstant.UPLOAD_LOG_RES) ||
//					str_prefix.equals(IDeviceConstant.UPLOAD_RADIO_RES)
//					) {
//				LOG.info("设备等自定义数据" + str_prefix + "操作 msg=" + msg);
//				String[] strArr_msg = str_msg.split(",");
//				String deviceUuid = strArr_msg[1];
//				DeviceInfo deviceInfo = deviceService.getDeviceInfoByUuid(deviceUuid);
//				if (!UtilTool.isNull(deviceInfo)){
//					log.debug("设备deviceUuid:"+deviceUuid+"存在,处理上行报文");
//					deviceService.backReport((JSONObject) JSONObject.toJSON(msg));
//				}else {
//					LOG.debug("设备uuid="+deviceUuid+"没有收录在数据库");
//				}
//			}
//		}
//
//		return MessageCallback.Action.CommitSuccess;
//	}
//
//	/**
//	 * _系统Topic 设备上下线状态
//	 * @param messageToken
//	 * @return
//	 */
//	private Action consumeOnOffLine(final MessageToken messageToken) {
//		Message msg = messageToken.getMessage();
//		String str_msg = new String(msg.getPayload());
//		LOG.info("系统Topic 设备上下线状态 msg=" + msg);
//
//		JSONObject jsonObj = JSON.parseObject(str_msg);
//		String str_devNO = jsonObj.getString("deviceName");
//		String str_status = jsonObj.getString("status");
//		Date dt_lastTime = jsonObj.getDate("lastTime");
//		String ip = jsonObj.getString("clientIp");
//		long l_generateTime = msg.getGenerateTime();
//		Date now = new Date(l_generateTime);
//
//		// 插入设备日志
//		DeviceLog deviceLog = new DeviceLog();
//		deviceLog.setfDeviceNo(str_devNO);
//		deviceLog.setfType(DeviceLogTypeEnum.ON_OFF.getValue());
//		deviceLog.setfLog(str_status);
//		deviceLog.setfTime(dt_lastTime);
//		deviceLog.setfCreateTime(now);
//		deviceLogService.insert(deviceLog);
//
//		// 修改设备信息
//		int i_deviceState = "online".equals(str_status) ? 1 : "offline".equals(str_status) ? 2 : 0;
//		deviceService.updateState(str_devNO, i_deviceState,ip);
//
//		deviceService.alterPowerState(str_devNO);
//
//		return MessageCallback.Action.CommitSuccess;
//	}
//
//	/**
//	 * _系统Topic 设备生命周期变更
//	 * @param messageToken
//	 * @return
//	 */
//	private Action consumeLifecycle(final MessageToken messageToken) {
//		Message msg = messageToken.getMessage();
//		String str_msg = new String(msg.getPayload());
//		LOG.info("系统Topic 设备生命周期变更 msg=" + msg);
//
//		JSONObject jsonObj = JSON.parseObject(str_msg);
//		String str_devNO = jsonObj.getString("deviceName");
//		String str_action = jsonObj.getString("action");
//		Date dt_messageCreateTime = jsonObj.getDate("messageCreateTime");
//		long l_generateTime = msg.getGenerateTime();
//		Date now = new Date(l_generateTime);
//
//		DeviceLog deviceLog = new DeviceLog();
//		deviceLog.setfDeviceNo(str_devNO);
//		deviceLog.setfType(DeviceLogTypeEnum.LIFE_CYCLE.getValue());
//		deviceLog.setfLog(str_action);
//		deviceLog.setfTime(dt_messageCreateTime);
//		deviceLog.setfCreateTime(now);
//		deviceLogService.insert(deviceLog);
//		return MessageCallback.Action.CommitSuccess;
//	}
//
//	@PostConstruct
//	private void recvDeviceLog() {
//		// endPoint: https://${uid}.iot-as-http2.${region}.aliyuncs.com
//		String endPoint = "https://" + aliyunConfig.getUid() + ".iot-as-http2." + aliyunConfig.getRegionId() + ".aliyuncs.com";
//
//		// 连接配置
//		Profile profile = Profile.getAccessKeyProfile(endPoint, aliyunConfig.getRegionId(), aliyunConfig.getAccessKeyId(), aliyunConfig.getAccessKeySecret());
//
//		// 构造客户端
//		MessageClient messageClient = MessageClientFactory.messageClient(profile);
//
//		// 自定义Topic 设备归还等操作
//		messageClient.setMessageListener("/" + aliyunConfig.getProductKey() + "/+/update", this::consumeCustom); // #代表本级及下级所有类目 +代表本级所有类目
//
//		// 系统Topic 设备上下线状态
//		messageClient.setMessageListener("/as/mqtt/status/" + aliyunConfig.getProductKey() + "/#", this::consumeOnOffLine);
//
//		// 系统Topic 设备生命周期变更
//		messageClient.setMessageListener("/sys/" + aliyunConfig.getProductKey() + "/+/thing/lifecycle", this::consumeLifecycle);
//
//		// 数据接收
//		messageClient.connect(messageToken -> {
//			Message m = messageToken.getMessage();
//			System.out.println("connect() " + m);
//			log.info("\n"+"topic:"+m.getTopic()+"\n"+
//					"qos:"+m.getQos()+"\n"+
//					"message:"+m.getMessageId()+"\n"+
//					"payload:"+String.valueOf(m.getPayload())+"\n");
//			return MessageCallback.Action.CommitSuccess;
//		});
//	}
//
//	public static void main_(String[] args) {
//		AliyunClientUtil.pub("a1PFLC4RGr8", "/a1PFLC4RGr8/64c9b1f/get", "5536440100010F00000000000000000002F00000000000000000000301000B23C2050000006A040000000000000000000005000000000000000000000600000000000000000000A5");
//	}
//
//}
