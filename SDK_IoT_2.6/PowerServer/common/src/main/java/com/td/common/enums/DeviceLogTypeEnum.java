package com.td.common.enums;

/**
 * _设备日志类型枚举
 * @author Administrator
 *
 */
public enum DeviceLogTypeEnum {

	/**
	 * _归还
	 */
	RETURN((short)1),

	/**
	 * _上下线状态
	 */
	ON_OFF((short)2),

	/**
	 * _生命周期变更
	 */
	LIFE_CYCLE((short)3);

	private short value;

	DeviceLogTypeEnum(short value) {
		this.value = value;
	}

	public short getValue() {
		return value;
	}

}
