package com.td.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
/**

* @Description:    公共工具类

* @Author:         chenqp

* @CreateDate:     2019/3/27 15:22

* @Version:        1.0

*/
public class CommonHelper {

	/**
	 * 计算循环次数
	 * @param total	总数
	 * @param size	每次循环包含数
	 * @return
	 */
	public static Long calculateCount(Long total, Long size) {
		long count = 0;
		if (total > 0) {
			count = 1;
			if (total > size) {
				count = ((total % size == 0) ? (total / size) : (total / size + 1));
			}
		}
		return count;
	}
	
	/**
	 * 获取IP
	 * @return
	 */
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (!StringUtils.isBlank(ip) && ip.indexOf(",") != -1) {
			ip = ip.split(",")[0];
		}
		return ip;
	}
}
