package com.td.task.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.iot.api.message.callback.MessageCallback;
import com.aliyun.openservices.iot.api.message.entity.MessageToken;
import com.td.common.constant.IDeviceConstant;
import com.td.common.enums.DeviceLogTypeEnum;
import com.td.common_service.model.DeviceInfo;
import com.td.common_service.service.DeviceService;
import com.td.common_service.service.QiwiPaymentsService;
import com.td.common_service.service.impl.OrderPayServiceImpl;
import com.td.task.model.DeviceLog;
import com.td.task.service.IDeviceLogService;
import com.td.util.UtilTool;
import com.td.util.config.AliyunConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.qpid.jms.JmsConnection;
import org.apache.qpid.jms.JmsConnectionListener;
import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MQTT修改
 */
@Component
@Slf4j
public class DeviceLogSchedule2 {

	@Autowired
	private QiwiPaymentsService qiwiPaymentsService;
	@Autowired
	private OrderPayServiceImpl orderPayService;

	@Autowired
	private  IDeviceLogService deviceLogService;
	@Autowired
	private DeviceService deviceService;


	private final static Logger logger = LoggerFactory.getLogger(DeviceLogSchedule2.class);

	//业务处理异步线程池，线程池参数可以根据您的业务特点调整，或者您也可以用其他异步方式处理接收到的消息。
	private final static ExecutorService executorService = new ThreadPoolExecutor(
			Runtime.getRuntime().availableProcessors(),
			Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(50000));

	@PostConstruct
	private void starter(){
		qiwiPaymentsService.qiwiPaymentHandler();
		orderPayService.rentOrderHandler();
	}

	@PostConstruct
	public void recvDeviceLog(){
		try {
			//参数说明，请参见文档：AMQP客户端接入说明。
			String consumerGroupId = AliyunConfig.CONSUMER_GROUP_ID;
			//iotInstanceId：购买的实例请填写实例ID，公共实例请填空字符串""。
			String iotInstanceId = "";
			long timeStamp = System.currentTimeMillis();
			//签名方法：支持hmacmd5，hmacsha1和hmacsha256。
			String signMethod = "hmacsha1";
			//控制台服务端订阅中消费组状态页客户端ID一栏将显示clientId参数。
			//建议使用机器UUID、MAC地址、IP等唯一标识等作为clientId。便于您区分识别不同的客户端。
			InetAddress localhost = InetAddress.getLocalHost();
			byte[] address = localhost.getAddress();
			String clientId = AliyunConfig.CLIENT_ID;

			//UserName组装方法，请参见文档：AMQP客户端接入说明。
			String userName = clientId + "|authMode=aksign"
					+ ",signMethod=" + signMethod
					+ ",timestamp=" + timeStamp
					+ ",authId=" + AliyunConfig.ACCESS_KEY_ID
					+ ",iotInstanceId=" + iotInstanceId
					+ ",consumerGroupId=" + consumerGroupId
					+ "|";
			//password组装方法，请参见文档：AMQP客户端接入说明。
			String signContent = "authId=" + AliyunConfig.ACCESS_KEY_ID + "&timestamp=" + timeStamp;
			String password = doSign(signContent, AliyunConfig.ACCESS_KEY_SECRET, signMethod);
			//按照qpid-jms的规范，组装连接URL。
			String connectionUrl = "failover:(amqps://"+AliyunConfig.UID+".iot-amqp."+AliyunConfig.REGION_ID+".aliyuncs.com:5671?amqp.idleTimeout=80000)"
					+ "?failover.reconnectDelay=30";

			Hashtable<String, String> hashtable = new Hashtable<>();
			hashtable.put("connectionfactory.SBCF", connectionUrl);
			hashtable.put("queue.QUEUE", "default");
			hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
			Context context = new InitialContext(hashtable);
			ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
			Destination queue = (Destination) context.lookup("QUEUE");
			// Create Connection
			Connection connection = cf.createConnection(userName, password);
			((JmsConnection) connection).addConnectionListener(myJmsConnectionListener);
			// Create Session
			// Session.CLIENT_ACKNOWLEDGE: 收到消息后，需要手动调用message.acknowledge()。
			// Session.AUTO_ACKNOWLEDGE: SDK自动ACK（推荐）。
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			// Create Receiver Link
			MessageConsumer consumer = session.createConsumer(queue);
			consumer.setMessageListener(messageListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private MessageListener messageListener = new MessageListener() {
		@Override
		public void onMessage(Message message) {
			try {
				//1.收到消息之后一定要ACK。
				// 推荐做法：创建Session选择Session.AUTO_ACKNOWLEDGE，这里会自动ACK。
				// 其他做法：创建Session选择Session.CLIENT_ACKNOWLEDGE，这里一定要调message.acknowledge()来ACK。
				// message.acknowledge();
				//2.建议异步处理收到的消息，确保onMessage函数里没有耗时逻辑。
				// 如果业务处理耗时过程过长阻塞住线程，可能会影响SDK收到消息后的正常回调。
				executorService.submit(() -> processMessage(message));
			} catch (Exception e) {
				logger.error("submit task occurs exception ", e);
			}
		}
	};

	/**
	 * 在这里处理您收到消息后的具体业务逻辑。
	 */
	private void processMessage(Message message) {
		try {
			byte[] body = message.getBody(byte[].class);
			//消息内容
			String content = new String(body);
			if("\n".equals(content.substring(content.length()-1))){
				content = content.substring(0,content.length()-1);
			}
			String topic = message.getStringProperty("topic");
			String messageId = message.getStringProperty("messageId");
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("topic",topic);
			jsonObject.put("messageId",messageId);
			jsonObject.put("content",content);

			//System.out.println(content);
			//log.info("\n"+"receive message" + "\n"
			//		+ "topic = " + topic + "\n"
			//		+ "messageId = " + messageId + "\n"
			//		+ "content = " + content);
			String[] split = topic.split("/");
			log.info("topic12523: "+topic);
			switch (split.length){
				case 4:
					if(AliyunConfig.PRODUCT_KEY.equals(split[1])
							&& "update".equals(split[3])){
						log.info("设备命令");
						consumeCustom(jsonObject,content);
					}
					break;
				case 6:
					if("as".equals(split[1])
							&& "mqtt".equals(split[2])
							&& "status".equals(split[3])
							&& AliyunConfig.PRODUCT_KEY.equals(split[4])) {
						log.info("设备上下线");
						consumeOnOffLine(content);

					}
					break;
				default:
					log.info("topic未做处理");
			}
			// 自定义Topic 设备归还等操作
			//messageClient.setMessageListener("/" + aliyunConfig.getProductKey() + "/+/update", this::consumeCustom); // #代表本级及下级所有类目 +代表本级所有类目

			// 系统Topic 设备上下线状态
			//messageClient.setMessageListener("/as/mqtt/status/" + aliyunConfig.getProductKey() + "/#", this::consumeOnOffLine);
		} catch (Exception e) {
			log.error("processMessage occurs error ", e);
		}
	}

	public static void main(String[] args) throws UnknownHostException, SocketException {
		InetAddress localhost = InetAddress.getLocalHost();

		System.out.println(getRealIp());
		//String s = "/as/mqtt/status/a4wOOtSxL5i/999133041313486";
		//String[] split = s.split("/");
		//System.out.println(split.length);
		//for (String s1 : split) {
		//	System.out.println(s1);
		//}
		//String s1 = "/a4wOOtSxL5i/999133041313486/update";
		//String[] split1 = s1.split("/");
		//System.out.println(split1.length);
		//for (String s2 : split1) {
		//	System.out.println(s2);
		//}
	}
	public static String getRealIp() throws SocketException {
		String localip = null;// 本地IP，如果没有配置外网IP则返回它
		String netip = null;// 外网IP

		Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		boolean finded = false;// 是否找到外网IP
		while (netInterfaces.hasMoreElements() && !finded) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> address = ni.getInetAddresses();
			while (address.hasMoreElements()) {
				ip = address.nextElement();
				if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
					netip = ip.getHostAddress();
					finded = true;
					break;
				} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
						&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
					localip = ip.getHostAddress();
				}
			}
		}

		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}

	private static JmsConnectionListener myJmsConnectionListener = new JmsConnectionListener() {
		/**
		 * 连接成功建立。
		 */
		@Override
		public void onConnectionEstablished(URI remoteURI) {
			logger.info("onConnectionEstablished, remoteUri:{}", remoteURI);
		}

		/**
		 * 尝试过最大重试次数之后，最终连接失败。
		 */
		@Override
		public void onConnectionFailure(Throwable error) {
			logger.error("onConnectionFailure, {}", error.getMessage());
		}

		/**
		 * 连接中断。
		 */
		@Override
		public void onConnectionInterrupted(URI remoteURI) {
			logger.info("onConnectionInterrupted, remoteUri:{}", remoteURI);
		}

		/**
		 * 连接中断后又自动重连上。
		 */
		@Override
		public void onConnectionRestored(URI remoteURI) {
			logger.info("onConnectionRestored, remoteUri:{}", remoteURI);
		}

		@Override
		public void onInboundMessage(JmsInboundMessageDispatch envelope) {
		}

		@Override
		public void onSessionClosed(Session session, Throwable cause) {
		}

		@Override
		public void onConsumerClosed(MessageConsumer consumer, Throwable cause) {
		}

		@Override
		public void onProducerClosed(MessageProducer producer, Throwable cause) {
		}
	};

	/**
	 * password签名计算方法，请参见文档：AMQP客户端接入说明。
	 */
	private static String doSign(String toSignString, String secret, String signMethod) throws Exception {
		SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), signMethod);
		Mac mac = Mac.getInstance(signMethod);
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(toSignString.getBytes());
		return Base64.encodeBase64String(rawHmac);
	}

	/**
	 * _自定义Topic 设备归还等操作
	 * @param msg
	 * @return
	 */
	private MessageCallback.Action consumeCustom(final JSONObject message,final String msg) {
		log.debug("自定义Topic msg=" + msg);

		if (msg != null && msg.length() > 4) {
			String str_prefix = msg.substring(0, 3);
			if (	str_prefix.equals(IDeviceConstant.RETURN_COMMAND_START) ||
					str_prefix.equals(IDeviceConstant.DTU_COMMAND_START) ||
					str_prefix.equals(IDeviceConstant.INFO_UPLOAD_COMMAND_START) ||
					str_prefix.equals(IDeviceConstant.P2P_COMMAND_START) ||
					str_prefix.equals(IDeviceConstant.CHECK_TIME) ||
					str_prefix.equals(IDeviceConstant.DTU_UPDATE_START) ||
					str_prefix.equals(IDeviceConstant.UPLOAD_LOG_RES) ||
					str_prefix.equals(IDeviceConstant.UPLOAD_RADIO_RES)
			) {
				log.info("设备等自定义数据" + str_prefix + "操作 msg=" + msg);
				String[] strArr_msg = msg.split(",");
				String deviceUuid = strArr_msg[1];
				DeviceInfo deviceInfo = deviceService.getDeviceInfoByUuid(deviceUuid);
				if (!UtilTool.isNull(deviceInfo)){
					log.debug("设备deviceUuid:"+deviceUuid+"存在,处理上行报文");
					//deviceService.backReport((JSONObject) JSONObject.toJSON(msg));
					deviceService.backReport(message,msg);
				}else {
					log.debug("设备uuid="+deviceUuid+"没有收录在数据库");
				}
			}
		}

		return MessageCallback.Action.CommitSuccess;
	}

	/**
	 * _系统Topic 设备上下线状态
	 * @param msg
	 * @return
	 */
	private MessageCallback.Action consumeOnOffLine(final String msg) {

		log.info("系统Topic 设备上下线状态 msg=" + msg);

		JSONObject jsonObj = JSON.parseObject(msg);
		String str_devNO = jsonObj.getString("deviceName");
		String str_status = jsonObj.getString("status");
		Date dt_lastTime = jsonObj.getDate("lastTime");
		String ip = jsonObj.getString("clientIp");
		Date now = jsonObj.getDate("utcTime");

		// 插入设备日志
		DeviceLog deviceLog = new DeviceLog();
		deviceLog.setfDeviceNo(str_devNO);
		deviceLog.setfType(DeviceLogTypeEnum.ON_OFF.getValue());
		deviceLog.setfLog(str_status);
		deviceLog.setfTime(dt_lastTime);
		//deviceLog.setfCreateTime(now);
		deviceLogService.insert(deviceLog);

		// 修改设备信息
		int i_deviceState = "online".equals(str_status) ? 1 : "offline".equals(str_status) ? 2 : 0;
		deviceService.updateState(str_devNO, i_deviceState,ip);

		//deviceService.alterPowerState(str_devNO);

		return MessageCallback.Action.CommitSuccess;
	}


}
