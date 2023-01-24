package com.td.util.device;

import com.td.util.HexToBinaryUtils;
import com.td.util.StringUtils;
import com.td.util.aliyun.iot.AliyunClientUtil;
import com.td.util.config.AliyunConfig;
import com.td.util.config.DeviceEjectConfig;
import com.td.util.interfaces.ITcpNettyCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * 设备实用类
 * @author Administrator
 *
 */
@Slf4j
public class DeviceUtil {

	/**
	 *
	 */
	private static final String TOPIC_PATTERN = "/" + AliyunConfig.PRODUCT_KEY + "/%s/get";


	private static NetworkChannelType netWorkChannel_Mode = NetworkChannelType.ALIMQTT;//默认阿里云

	public static List<String> command103 = new ArrayList<String>(){{add("5503");add("5531");add("5533");add("5537");}};
	public static List<String> command203 = new ArrayList<String>(){{add("");add("");}};
	public static List<String> command303 = new ArrayList<String>(){{add("5536");add("");}};

	private static ITcpNettyCallback tcpNetty = null;

	public static void setNetWorkChannelMode(int mode){
		netWorkChannel_Mode.setIndex(mode);
	}

	public static void setTcpNettyCallback(ITcpNettyCallback tcpNetty_){
		tcpNetty = tcpNetty_;
	}
	/**
	 *
	 * @param strs
	 * @return
	 */
	public static final String getDeviceEjectRedisName(String... strs) {
		return DeviceEjectConfig.REDIS_KEY + StringUtils.combineStringArray(',', strs); // IConstant.DEGREE
	}

	/**
	 * 获取弹出命令
	 * @param uuId
	 * @param machineId
	 * @param positionNo
	 * @return
	 */
	public static final String getEjectCommand(String uuId,final String machineId, final String positionNo, final String hardVersion) {
		String str_ejectCommand = null;

		StringBuilder params = new StringBuilder();
		params.append("5531");// 命令头 以及 命令字

		params.append("03");// 数据长度
		params.append(machineId);// 从机id
		log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<"+positionNo);
		if (positionNo.length() == 1) {
			params.append("0" + positionNo);// 仓位编号
		} else {
			params.append(positionNo);// 仓位编号
		}

		params.append("01");// 充电宝充电开关控制
		params.append(HexToBinaryUtils.getCheckCode(params.toString()));// 添加校验码
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+positionNo);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>commad:"+params.toString());
		if (validateVersion(hardVersion)) { // 9口设备 v206版本以前的命令
			str_ejectCommand = "103," + uuId + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		} else {
			str_ejectCommand = "103," + uuId + "," + new Date().getTime() / 1000 + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		}
		return str_ejectCommand;
	}

	/**
	 * 获取弹出命令
	 * @param uuId
	 * @param machineId
	 * @param positionNo
	 * @return
	 */
	public static final String getEjectCommand(String execTime,String uuId,final String machineId, final String positionNo, final String hardVersion) {
		String str_ejectCommand = null;

		StringBuilder params = new StringBuilder();
		params.append("5531");// 命令头 以及 命令字

		params.append("03");// 数据长度
		params.append(machineId);// 从机id
		log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<"+positionNo);
		if (positionNo.length() == 1) {
			params.append("0" + positionNo);// 仓位编号
		} else {
			params.append(positionNo);// 仓位编号
		}

		params.append("01");// 充电宝充电开关控制
		params.append(HexToBinaryUtils.getCheckCode(params.toString()));// 添加校验码
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+positionNo);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>commad:"+params.toString());
		if (validateVersion(hardVersion)) { // 9口设备 v206版本以前的命令
			str_ejectCommand = "103," + uuId + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		} else {
			str_ejectCommand = "103," + uuId + "," + execTime + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		}
		return str_ejectCommand;
	}

	/**
	 * 弹出设备
	 * @param uuId
	 * @param machineId
	 * @param positionNo
	 * @return
	 */
	public static final boolean eject(String uuId, final String machineId, final String positionNo, final String hardVersion) {
//		if (org.apache.commons.lang3.StringUtils.isBlank(str_result)) {
		String str_ejectCommand = DeviceUtil.getEjectCommand(uuId, machineId, positionNo, hardVersion);
		log.info("发送的消息为："+str_ejectCommand);
		return pubCommand(uuId, str_ejectCommand);
	}
	/**
	 * 弹出设备
	 * @param uuId
	 * @param machineId
	 * @param positionNo
	 * @return
	 */
	public static final boolean eject(String exceTime,String uuId, final String machineId, final String positionNo, final String hardVersion) {
//		if (org.apache.commons.lang3.StringUtils.isBlank(str_result)) {
		String str_ejectCommand = DeviceUtil.getEjectCommand(exceTime,uuId, machineId, positionNo, hardVersion);
		log.info("发送的消息为："+str_ejectCommand);
		return pubCommand(uuId, str_ejectCommand);
	}

	/**
	 * 弹出设备
	 * @param uuId
	 * @param machineId
	 * @param positionNo
	 * @return
	 */
	public static final boolean openLock(String uuId, final String machineId, final String positionNo) {
//		if (org.apache.commons.lang3.StringUtils.isBlank(str_result)) {
		StringBuffer str_openLockCommand = new StringBuffer();
		str_openLockCommand.append("107," + uuId + ",");
		if(machineId.length() == 1){
			str_openLockCommand.append("0" + machineId);
		}else{
			str_openLockCommand.append(machineId);
		}
		if(positionNo.length()==1){
			str_openLockCommand.append("0" + positionNo);
		}else{
			str_openLockCommand.append(positionNo);
		}

		log.info("发送的消息为：" + str_openLockCommand);
		return pubCommand(uuId, str_openLockCommand.toString());
	}


	/**
	 *  下发清除仓位的归还结果
	 * @param uuId
	 * @param machineId
	 * @param positionNo
	 * @return
	 */
	public static final boolean cleanPos(String uuId, String machineId, String positionNo, String state, final String hardVersion) {
		String str_ejectCommand = null;

		StringBuilder params = new StringBuilder();
		params.append(state);// 命令头 以及 命令字  5537归还，5533租借

		params.append("02");// 数据长度
		params.append(machineId);// 从机id
		if (positionNo.length() == 1) {
			params.append("0" + positionNo);// 仓位编号
		} else {
			params.append(positionNo);// 仓位编号
		}
		params.append(HexToBinaryUtils.getCheckCode(params.toString()));// 添加校验码
		log.info("下发的命令参数为："+params);
		if (validateVersion(hardVersion)) { // 9口设备 v206版本以前的命令
			str_ejectCommand = "103," + uuId + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		} else {
			str_ejectCommand = "103," + uuId + "," + new Date().getTime() / 1000 + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		}
		log.info("下发清除的命令参数为："+str_ejectCommand);
/*
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
*/
		return pubCommand(uuId, str_ejectCommand);
	}


	/**
	 * 返回发送的命令
	 * @param uuId
	 * @param machineId
	 * @param positionNo
	 * @param state
	 * @return
	 */
	public static final String getCleanPosMsg(String uuId, String machineId, String positionNo, String state, final String hardVersion){
		String str_ejectCommand = null;

		StringBuilder params = new StringBuilder();
		params.append(state);// 命令头 以及 命令字  5537归还，5533租借

		params.append("02");// 数据长度
		params.append(machineId);// 从机id
		if (positionNo.length() == 1) {
			params.append("0" + positionNo);// 仓位编号
		} else {
			params.append(positionNo);// 仓位编号
		}
		params.append(HexToBinaryUtils.getCheckCode(params.toString()));// 添加校验码
		log.info("下发的命令参数为："+params);
		if (validateVersion(hardVersion)) { // 9口设备 v206版本以前的命令
			str_ejectCommand = "103," + uuId + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		} else {
			str_ejectCommand = "103," + uuId + "," + new Date().getTime()/1000 + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		}
		return str_ejectCommand;
	}

	/**
	 * 发送104校验时间
	 * @param uuId
	 * @param msg
	 * @return
	 */
	public static final boolean checkTime(String uuId,String msg){
		log.info("104参数："+msg);
		return pubCommand(uuId, msg);
	}

	/**
	 * 充电指令
	 */
	public static final String chargeBattery(String uuId,final String machineId, final String positionNo, final boolean status, final String hardVersion){
		String command = "";

		StringBuilder params = new StringBuilder();
		params.append("5503");// 命令头 以及 命令字

		params.append("03");// 数据长度
		params.append(machineId);// 从机id
		log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<"+positionNo);
		if (positionNo.length() == 1) {
			params.append("0" + positionNo);// 仓位编号
		} else {
			params.append(positionNo);// 仓位编号
		}

		params.append(status?"01":"00");// 充电宝充电开关控制
		params.append(HexToBinaryUtils.getCheckCode(params.toString()));// 添加校验码
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+positionNo);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>commad:"+params.toString());
		if (validateVersion(hardVersion)) { // 9口设备 v206版本以前的命令
			command = "103," + uuId + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		} else {
			command = "103," + uuId + "," + new Date().getTime() / 1000 + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		}
		return command;
	}

	/**
	 *  下发升级
	 */
	public static final boolean deviceUpgrade(String uuId,String type, String ip, String username,String password,String filename) {
		//101,{deviceID},IP,用户名,密码,端口号，文件名
		String str_ejectCommand = "101," + uuId + "," + type + "," + ip + "," + username + "," + password + "," + filename;// 直接传递base64之后的字符串
		return pubCommand(uuId, str_ejectCommand);
	}

	/**
	 * 远程上传日志
	 * @param uuId
	 * @param ip
	 * @param username
	 * @param password
	 * @return
	 */
	public static final boolean uploadLog(String uuId, String ip, String username, String password) {
		String str_ejectCommand = "106," + uuId + "," + ip + "," + username + "," + password;
		return pubCommand(uuId, str_ejectCommand);
	}

	public static final boolean updateParameter(String uuId) {
		String str_ejectCommand = "111," + uuId + ",1";
		return pubCommand(uuId, str_ejectCommand);
	}

	/**
	 * 投放广告
	 * @param uuId
	 * @param type
	 * @param command
	 * @return
	 */
	public static final boolean uploadRadio(String uuId, String type, String command) {
		return pubCommand(uuId, command);
	}

	private static boolean senedMsgByTcpNetty(final String uuId,final String strCommand){
		if(tcpNetty!=null) {
			tcpNetty.sendMsg(uuId,strCommand);
			return  true;
		}
		return false;
	}

	/**
	 * 版本校验
	 * @return
	 */
	private static boolean validateVersion(String hardVersion) {
		if (StringUtils.isEmpty(hardVersion)) {
			return false;
		}
		String substring = hardVersion.substring(1);
		boolean device9 = hardVersion.contains("v2") && Integer.parseInt(substring) < 206; // 9口设备 v206版本
		boolean device12 = hardVersion.contains("v3") && Integer.parseInt(substring) < 304; // 12口设备 v304版本
		return device9 || device12;
	}

	/**
	 * 命令解析
	 */
	public static String commandParse(String command) {
		String[] split = command.split(",");
		command = split[split.length - 1];
		byte[] decode = Base64.getMimeDecoder().decode(command);
		return com.td.util.HexToBinaryUtils.bytesToHexString(decode);
	}

	/**
	 * 发布指令
	 * @param uuId 设备号
	 * @param command 命令
	 * @return 结果
	 */
	public static boolean pubCommand(String uuId, String command) {
		if (StringUtils.isNotEmpty(command)) {
			if (netWorkChannel_Mode == NetworkChannelType.ALIMQTT)
				return AliyunClientUtil.pub(AliyunConfig.PRODUCT_KEY, String.format(TOPIC_PATTERN, uuId), command);
			if (netWorkChannel_Mode == NetworkChannelType.NETTYTCP) {
				return senedMsgByTcpNetty(uuId, command);
			}
		}
		return false;
	}

	public static final String encryCommand(String uuId,final String commandHead, final String command) {
		String str_ejectCommand = null;

		StringBuilder params = new StringBuilder();
		params.append(commandHead);// 命令头 以及 命令字

		params.append(HexToBinaryUtils.getHex(command.length()/2+""));// 数据长度
		params.append(command);

		params.append(HexToBinaryUtils.getCheckCode(params.toString()));// 添加校验码
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>commad:"+params.toString());
		str_ejectCommand = uuId + "," + HexToBinaryUtils.getHexString(params.toString());// 直接传递base64之后的字符串
		if (command303.contains(commandHead)) {
			str_ejectCommand = "303," + str_ejectCommand;
		}

		return str_ejectCommand;
	}

	public static void main(String[] args) {
		//DeviceUtil.cleanPos("862506049385829", "01", "1", "5533","v213");
		//DeviceUtil.cleanPos("CFdNzAs0qjCU2bGDHgGL000000", "01", "1", "5533","v206");
		//DeviceUtil.cleanPos("a1fe2TxOa3N", "01", "1", "5533","v206");
		//openLock("862506049385829","01","0");
		//eject("862506049385829","01","1","v206");
		//pubCommand("862506049385829",		chargeBattery("862506049385829","01","1",false,"v206"));
		String cmd = "55350101";//55-01-0d-01-31-31-31-31-32-32-32-32-33-33-33-33-a7
		cmd = "55-01-0d-01-31-31-31-31-32-32-32-32-33-33-33-33-a7".replace("-","");
		cmd = cmd+HexToBinaryUtils.getCheckCode(cmd);
		cmd=HexToBinaryUtils.getHexString(cmd);
		System.out.println("cmd: "+cmd);
		//cmd=204 + "," + "862506049385829" + "," + "check" + "," + new Date().getTime();
		//pubCommand("862506049385829","103,862506049385829,"+new Date().getTime()+","+cmd);
		//pubCommand("862506049385829","103,862506049385829,"+new Date().getTime()/1000+","+cmd);
		//pubCommand("862506049385829","103,862506049385829,"+new Date().getTime()+",VTEDAQEAmQ==");
		//pubCommand("862506049385829","103,862506049385829,"+new Date().getTime() / 1000+",VTEDAQEAmQ==");
		//pubCommand("CFdNzAs0qjCU2bGDHgGL000000","103,CFdNzAs0qjCU2bGDHgGL000000,"+new Date().getTime()+",VTEDAQEAmQ==");
		//pubCommand("CFdNzAs0qjCU2bGDHgGL000000","103,CFdNzAs0qjCU2bGDHgGL000000,"+new Date().getTime() / 1000+",VTEDAQEAmQ==");
		//pubCommand("a1fe2TxOa3N","103,a1fe2TxOa3N,"+new Date().getTime()+",VTEDAQEAmQ==");
		//pubCommand("a1fe2TxOa3N","103,a1fe2TxOa3N,"+new Date().getTime() / 1000+",VTEDAQEAmQ==");
		//pubCommand("862506049385829","108,862506049385829");
		//pubCommand("987654321012000",openLock("862506049385829","01","1"));
		//pubCommand("987654321012000",getEjectCommand("862506049385829","01","1",""));
		System.out.println(""+ getEjectCommand("862506049385829","01","1","v205"));
		//pubCommand("862506049385829",getEjectCommand("862506049385829","01","1","v205"));
		//openLock("862506049385829","01","1");

	}
}
