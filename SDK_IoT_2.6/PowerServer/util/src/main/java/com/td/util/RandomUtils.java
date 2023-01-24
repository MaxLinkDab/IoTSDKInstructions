package com.td.util;

import java.util.Random;
/**

* @Description:   随机数工具类
* @Author:       chen
* @CreateDate:     2019/4/3 9:23
* @Version:        1.0

*/
public class RandomUtils {
	public static Random random = new Random();
	public static Random timeSeedRandom = new Random(System.currentTimeMillis());

	/**
	 * 随机指定长度的字符串，包含数字字符串
	 * @param length
	 * @return
	 */
	public static String getRandom(int length) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < length; i++) {
			boolean isChar =  (random.nextInt(2) % 2 == 0); // 输出字母还是数字
			if (isChar) { // 字符串
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
				ret.append((char) (choice + random.nextInt(26)));
			} else { // 数字
				ret.append(Integer.toString(random.nextInt(10)));
			}
		}
		return ret.toString();
	}
	
	/**
	 * 随机指定长度的字符串，只包含数字
	 * @param length
	 * @return
	 */
	public static String getRandomNumeric(int length) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < length; i++) {
			ret.append(Integer.toString(random.nextInt(10)));
		}
		return ret.toString();
	}
	
	/**
	 * 随机指定区间的整数
	 * @param startInclude	起始值，包含
	 * @param endExclude	结束值，不包含
	 * @return
	 */
	public static int nextInt(int startInclude, int endExclude) {
		return nextInt(startInclude, endExclude, timeSeedRandom);
	}
	
	/**
	 * 随机指定区间的整数
	 * @param startInclude	起始值，包含
	 * @param endExclude	结束值，不包含
	 * @param random		指定的随机对象
	 * @return
	 */
	public static int nextInt(int startInclude, int endExclude, Random random) {
		if(startInclude > endExclude) 
			throw new IllegalArgumentException("start must less than end.");
		if(startInclude == endExclude)
			return startInclude;
		return random.nextInt(endExclude-startInclude) + startInclude;
	}
}
