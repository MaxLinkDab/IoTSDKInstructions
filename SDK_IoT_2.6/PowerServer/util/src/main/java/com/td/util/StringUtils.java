package com.td.util;

import com.td.util.vo.PositionVo;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @Description: 字符串工具类
 *
 * @Author: chenqp
 *
 * @CreateDate: 2019/3/27 15:23
 *
 * @Version: 1.0
 *
 */
public class StringUtils {

	public static final String EMPTY = "";

	/**
	 * 方法说明：验证对象是否为NULL，空字符串，空数组，空的Collection或Map(只有空格的字符串也认为是空串)
	 *
	 * @param obj     被验证的对象
	 * @param message 异常信息
	 */
	@SuppressWarnings("rawtypes")
	public static void notEmpty(Object obj, String message) {
		if (obj == null) {
			throw new IllegalArgumentException(message + " must be required.");
		}
		if (obj instanceof String && obj.toString().trim().length() == 0) {
			throw new IllegalArgumentException(message + " must be required.");
		}
		if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
			throw new IllegalArgumentException(message + " must be required.");
		}
		if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
			throw new IllegalArgumentException(message + " must be required.");
		}
		if (obj instanceof Map && ((Map) obj).isEmpty()) {
			throw new IllegalArgumentException(message + " must be required.");
		}
	}

	public static boolean isNotEmpty(String str) {
		return !StringUtils.isEmpty(str);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static String trim(String str) {
		return str == null ? EMPTY : str.trim();
	}

	public static boolean equals(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equals(str2);
	}

	/**
	 * 方法说明：将map转换为key1=value1&key2=value2格式的字符串
	 *
	 * @param map
	 * @return
	 */
	public static String mapToString(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			buffer.append("&").append(entry.getKey()).append("=").append(entry.getValue());
		}
		return buffer.toString().substring(1);
	}

	/**
	 * 过滤微信表情
	 */
	public static String filterEmoji(String source) {
		if (source == null) {
			return source;
		}
		Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		Matcher emojiMatcher = emoji.matcher(source);
		if (emojiMatcher.find()) {
			source = emojiMatcher.replaceAll("*");
			return source;
		}
		return source;
	}

	/**
	 * 左补0
	 * @param str
	 * @param position 位数
	 * @return
	 */
	public static final String leftFillZero(String str, int position) {
		String str_ret = str;

		if (str != null && str.length() < position) {
//			String str_ = String.format("%02d", i);
			StringBuilder strBud = new StringBuilder();
			for (int i = 0, len = position - str.length(); i < len; i++) {
				strBud.append("0");
			}
			str_ret = strBud.append(str).toString();
		}

		return str_ret;
	}

	/**
	 * 按照合并字符合并字符串数组
	 * @param combineChar
	 * @param strings
	 * @return
	 */
	public static final String combineStringArray(char combineChar, String... strings) {
		String str = null;

		if (strings != null && strings.length > 0) {
			StringBuilder strBud = new StringBuilder();
			for (String string : strings) {
				strBud.append(string).append(combineChar);
			}
			str = strBud.deleteCharAt(strBud.length() - 1).toString();
		}

		return str;
	}

	/**
	 *  字符数组转成 字符- ,号隔开
	 */
	public static final String arrayToString(String... ids) {
		String idList = "";
		if(ids==null){
			return null;
		}
		for (String id: ids) {
			idList = idList+id+",";
		}
		if(StringUtils.isEmpty(idList)){
			return "";
		}else{
			return idList.substring(0,idList.length()-1);
		}

	}

	/**
	 * 通过positionUUid获取仓位和从机编号
	 * @param positionUuid
	 * @return
	 */
	public static PositionVo getPosition(String positionUuid) {
		Integer machineUuid = new Double(Math.ceil(Double.valueOf(positionUuid)/6)).intValue();
		positionUuid = String.valueOf(Integer.valueOf(positionUuid) - (machineUuid-1)*6);
		int length = String.valueOf(machineUuid).length();
		String m = String.valueOf(machineUuid);
		if (length < 2) {
			m = "0" + m;
		}
		return new PositionVo(positionUuid, m);
	}


	public static void main(String[] args) {
		PositionVo position = getPosition("1");
		System.out.println(position.getMachineUuid());
		System.out.println(position.getPositionUuid());
	}

	public static String removeTrim(String str){
		if(str.indexOf(".") > 0){
			str = str.replaceAll("0+?$", "");//去掉多余的0
			str = str.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
		return str;
	}

}
