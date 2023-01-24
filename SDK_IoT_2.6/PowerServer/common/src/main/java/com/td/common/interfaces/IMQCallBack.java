package com.td.common.interfaces;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author Administrator
 *
 */
public interface IMQCallBack {

	/**
	 * 
	 * @param jsonObj
	 * @return
	 */
	public boolean handle(JSONObject jsonObj);
	
}
