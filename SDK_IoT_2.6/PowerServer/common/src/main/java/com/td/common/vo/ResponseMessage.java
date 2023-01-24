package com.td.common.vo;

import com.td.common.enums.ErrorMsg;

import lombok.Data;

/**
 * 客户端接口响应
 * 
 * @author chen
 * @version 1.0 2015-9-18
 */
@Data
public class ResponseMessage extends Message {
	private static final long serialVersionUID = 27219325229L;

	private String code;

	private String message;

	public ResponseMessage() {
		setRespMsg(ErrorMsg.success);
	}

	public ResponseMessage(ErrorMsg err) {
		super();
		this.code = err.getCode();
		this.message = err.getMsg();
	}

	/**
	 * 处理成功
	 * 
	 * @return
	 */
	public boolean succeed() {
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
