package com.td.common.wechat;

import java.io.Serializable;

import com.td.common.enums.ErrorMsg;

/**
 * 
 * @author lv
 * 2017年3月24日
 * @param <T>
 * Description:
 */
public class WxResponseMessage<T> implements Serializable {
	private static final long serialVersionUID = 2721549932522353419L;

	private String code;

	private String message;

	private T result;

	public WxResponseMessage() {
		setRespMsg(ErrorMsg.success);
	}

	public WxResponseMessage(ErrorMsg err) {
		super();
		this.code = err.getCode();
		this.message = err.getMsg();
	}

	public WxResponseMessage(String code) {
		super();
		this.code = code;
	}

	public WxResponseMessage(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	/**
	 * 处理成功
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return ErrorMsg.success.getCode().equals(code);
	}

	public void setRespMsg(ErrorMsg msg) {
		this.code = msg.getCode();
		this.message = msg.getMsg();
	}

	public void setRespMsg(ErrorMsg msg,String customMsgText) {
		this.code = msg.getCode();
		this.message = customMsgText;
	}
}
