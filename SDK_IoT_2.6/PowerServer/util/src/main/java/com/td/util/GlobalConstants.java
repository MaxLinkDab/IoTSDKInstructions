package com.td.util;


/**
 * 各类配置信息,合并了原来的api配置, wap站配置.
 * 
 *
 * @author frezzlee
 * @created 2017年3月20日 下午2:17:43
 */
public class GlobalConstants {

	/**
	 * 用户登录后存入session的键
	 */
	public static final String SESSION_LOGIN_USER_NAME="LOGIN_USER";
	

	
	/*********************************微信顾客端相关配置*****************************************/
	/**
	 * 微信顾客端服务号 API调用所需的access_token
	 */
	public static final String WECHAT_ACCESS_TOKEN = "WECHAT_ACCESS_TOKEN";

	/**
	 * 微信小程序所需 access_token
	 */
	public static final String WXC_ACCESS_TOKEN = "WXC_ACCESS_TOKEN";

	/**
	 * 请求地址：生成带参数的二维码
	 */
	public static final String GET_WX_TWO_DIMENSIONAL_CODE="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";

	/**
	 * 请求地址：生成带参数的二维码 小程序
	 */
	public static final String GET_WXC_TWO_DIMENSIONAL_CODE="https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=";

	/**
	 *  请求地址：获取微信用户信息
	 */
	public static final String GET_WX_USERINFO_URL="https://api.weixin.qq.com/cgi-bin/user/info?access_token=";

	
	/**
	 * 当前会话对应的token, 没有登录就没有token.
	 */
	public static final  String USER_TOKEN="token";

	/**
	 * 微信API调用所需的用户授权refresh_token
	 */
	public static final String WECHAT_USER_REFRESH_TOKEN ="WECHAT_USER_REFRESH_TOKEN";
	/**
     * 微信API调用所需的access_token
     */
    public static final String WECHAT_JSAPI_TICKET ="WECHAT_JSAPI_TICKET";

	/**
	 * 用户访问历史url
	 */
	public static final String SESSION_HISTORY_URL_MAP="HISTORY_URL_MAP";

	/**
	 * 请求地址：发送模版消息（小程序）
	 */
	public static final String GET_WXC_MESSAGE_TEMPLATE_URL="https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=";

	/**
	 * 小程序模版id
	 */
	public static final String GET_WXC_TEMPLATE_ID="BGiTrrraxHAesxovhORHGnN-lIhxuUzVi5KPm8TkWZc";
	
}
