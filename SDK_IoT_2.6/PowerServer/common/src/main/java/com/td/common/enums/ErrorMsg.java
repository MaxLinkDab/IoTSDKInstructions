package com.td.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 错误码（枚举建议首字母大写，未大写的以后统一）
 * @author chen
 * @version 1.0 2015-9-18
 */
public enum ErrorMsg {
	// 错误码定义
	systemErr					("9999", "系统未知异常"), 
	failed						("9998", "失败"), 
	success						("0000", "成功"), 
	paramIsEmpty				("0001", "参数不能为空"), 
	paramIllegality				("0002", "参数非法"), 
	msgFormatErr				("0010", "消息格式错误"), 
	digestErr					("0015", "消息摘要不匹配"), 
	unSuportMessageType			("0016", "不支持的消息类型"), 
	unauthorized				("0017", "非法访问"),
	/**
	 * 表示： token 已过期，验证失败，需要重新登陆
	 */
	tokenOverdue				("0018", "登陆已过期，请重新登陆"),
	dataIsEmpty					("0019", "没有数据"),
	UnsupportLoginType			("0020", "不支持的登录方式"),
	devicUnexist			    ("0021", "设备不存在"),
	userUnpayOrder			    ("0022", "您还有未付款订单，请付款完再扫码租借"),
	/**
	 * 用户信息错误消息
	 */
	userUnexist			        ("0030 ", "用户不存在"),

	/**
	 * 订单错误码
	 */
	orderUnSettlement                        ("0040","订单未结算"),
	;

	/**
	 * 错误编号
	 */
	private String code;

	/**
	 * 错误提示
	 */
	private String msg;

	ErrorMsg(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * 静态缓存，提高效率
	 */
	private static Map<String, ErrorMsg> cache;
	static {
		cache = new HashMap<String, ErrorMsg>();
		ErrorMsg[] values = ErrorMsg.values();
		for (ErrorMsg error : values) {
			cache.put(error.code, error);
		}
	}

	/**
	 * <pre>
	 * 获取消息内容
	 * </pre>
	 * 
	 * @param code
	 * @return
	 */
	public static String getMsg(String code) {
		if (code != null && code.length() > 0) {
			ErrorMsg errorMsg = cache.get(code);
			if (errorMsg != null) {
				return errorMsg.getMsg();
			}
		}

		return ErrorMsg.systemErr.getMsg();
	}
}
