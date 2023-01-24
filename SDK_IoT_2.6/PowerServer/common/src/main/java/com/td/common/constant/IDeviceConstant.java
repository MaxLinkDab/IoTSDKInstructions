package com.td.common.constant;

/**
 * _设备日志常量
 * @author Administrator
 *
 */
public interface IDeviceConstant {

	/**
	 * _归还命令开头
	 */
	public static final String RETURN_COMMAND_START = "303";

	/**
	 * _DTU版本
	 */
	public static final String DTU_COMMAND_START = "302";

	/**
	 * _信息上报命令开头
	 */
	public static final String INFO_UPLOAD_COMMAND_START = "300";

	/**
	 * _串口透传响应命令开头
	 */
	public static final String P2P_COMMAND_START = "203";

	/**
	 * 校验时间
	 */
	public static final String CHECK_TIME = "104";

	/**
	 * 远程升级DTU响应
	 */
	public static final String DTU_UPDATE_START = "201";

	/**
	 * 远程上传日志响应
	 */
	public static final String UPLOAD_LOG_RES = "206";


	/**
	 * 广告投放响应
	 */
	public static final String UPLOAD_RADIO_RES = "208";

}
