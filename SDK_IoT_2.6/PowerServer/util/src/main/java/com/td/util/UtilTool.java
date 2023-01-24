package com.td.util;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class UtilTool {

	/**
	 * 判断是否为空
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj){
		if(obj == null || "null".equals(obj) || "".equals(obj)){
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 删除上传文件
	 * @param files
	 */
	public static void deleteFile(File... files) {
		for (File file : files) {
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * 获取当天日期
	 */
	public static String getToday(){
		String temp_str="";
		Date dt = new Date();
		//最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		temp_str=sdf.format(dt);
		return temp_str;
	}

	/**
	 * 获取ip地址
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getIp(HttpServletRequest request) throws Exception {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null) {
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
				int index = ip.indexOf(",");
				if (index != -1) {
					return ip.substring(0, index);
				} else {
					return ip;
				}
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (ip != null) {
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		ip = request.getHeader("Proxy-Client-IP");
		if (ip != null) {
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip != null) {
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		ip = request.getRemoteAddr();
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	/**
	 * 生成订单号
	 * @return
	 */
	public static String getOrderCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(DateToString(new Date(), "yyyyMMddHHmm"));
		sb.append(UtilTool.getRandomCode(12));
		return sb.toString();
	}

	/**
	 * 日期格式转换
	 *
	 * @param date
	 * @param fmt
	 * @return
	 */
	public static String DateToString(Date date, String fmt) {
		try {
			SimpleDateFormat ft = new SimpleDateFormat(fmt);
			String dd = ft.format(date);
			return dd;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 获取随机数列,例如六位随机码（028374）
	 *
	 * @param bit
	 *            位数
	 * @return
	 */
	public static String getRandomCode(int bit) {
		StringBuilder code = new StringBuilder("");
		for (int i = 0; i < bit; i++) {
			code.append(getRandom(10));
		}
		return code.toString();
	}

	/**
	 * 获取范围内的随机数
	 *
	 * @param scope
	 *            取值范围0~scope
	 * @return
	 */
	public static int getRandom(int scope) {
		Random random = new Random();
		int pos = random.nextInt(scope);
		return pos;
	}

	/**
	 * 计算两个时间搓之间的小时数
	 * @param hqtime
	 * @return
	 */
	public static int calcTime(long hqtime){
		int s = Math.toIntExact(Math.max((System.currentTimeMillis() - hqtime),0) / (1000 * 60 ));
		int time=0;
		if (s>5){
			time = s%60 == 0 ? (s/60) : (s/60)+1;
		}
		return time;
	}

	/**
	 * 计算两个时间搓之间的分钟数
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int calcTime(Timestamp startTime, Timestamp endTime){
		int time = Math.toIntExact((endTime.getTime() - startTime.getTime()) / (1000 * 60 ));
		return time;
	}

	/**
	 * 时间戳转换成日期格式字符串
	 * @param seconds
	 * @return
	 */
	public static String timeStamp2Date(Timestamp seconds) {
		String format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(seconds.getTime()));
	}

	/**
	 * 价格分转元
	 * @param price
	 * @return
	 */
	public static String intToStr(int price){
		DecimalFormat df=new DecimalFormat("0.00");
		String str = df.format((float)price/100);
		return str;
	}

	/**
	 * 获取几天前的日期
	 * @param data
	 * @return
	 */
	public static String getDate(int data,int type){
		Date date=new Date();//取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE,data);//把日期往后增加一天.整数往后推,负数往前移动
		date=calendar.getTime(); //这个时间就是日期往后推一天的结果
		String dateFormatType = "yyyy-MM-dd";
		if (type==1){
			dateFormatType = "yyyy-MM";
		}
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormatType);
		String dateString = formatter.format(date);
		System.out.println(dateString);
		return dateString;
	}

	/**
	 *double保留两位小数
	 */
	public static double doubleTodouble(double d) {
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.parseDouble(df.format(d));
	}

	/**
	 * 价格元转分
	 * @param price
	 * @return
	 */
	public static int strToInt(double price){
		DecimalFormat df = new DecimalFormat("#.00");
		price = Double.valueOf(df.format(price));
		int money = (int)(price * 100);
		return money;
	}

	/**
	 * 取整数
	 */
	public static int getFullInt(int a,int b){
		if (a % b !=0){
			return a/b + 1;
		}else {
			return a/b;
		}
	}

	/**
	 * 计算价格
	 * @return
	 */
	public static int calcMoney(long hqtime,int dayMaxPrice,int allMaxPrice,int price){
		System.out.println("\n");
		int money;
		int rentTime = calcTime(hqtime); //借出的小时数
		int dayTime = getFullInt(dayMaxPrice,price); //借出几个小时后能达到当日的最大数
		int allToday = getFullInt(allMaxPrice,dayMaxPrice);//押金没了，借出的最小天数
		int rentDay = rentTime/24; //借出多少天了
		int rentDaya = rentTime % 24; //借出的余小时数
		if (rentTime>=dayTime){
			if (rentDay==0){
				System.out.println(":1");
				money = dayMaxPrice;
			}else if (rentDay==1 && rentDaya ==0){
				System.out.println(":2");
				money = dayMaxPrice;
			}else if (rentDaya<=dayTime){
				System.out.println(":3");
				money = dayMaxPrice * rentDay + (rentTime -24*rentDay) *price;
			}else {
				System.out.println(":4");
				money = dayMaxPrice * (rentDay+1);
			}
		}else {
			money = rentTime * price;
		}
		System.out.println(":: "+rentTime + " "+ dayTime);
		if (money>allMaxPrice){
			money = allMaxPrice;
		}
		return money;
	}

	public static void main(String[] args) {

//		long time = Long.parseLong("1557729648000");
//		System.out.println(calcTime(time));
//		getDate(11,1);
		long a = System.currentTimeMillis()-60*1000*3;//5分钟内 2019-07-08 15:14:11
		a = System.currentTimeMillis()-60*1000*60*25;//5分钟内 2019-07-08 15:14:11
		long b = System.currentTimeMillis()-24*60*60*1000;
		long c = System.currentTimeMillis() -26*60*60*1000;
		long d = System.currentTimeMillis() -49*60*60*1000;
		long e = System.currentTimeMillis() -72*60*60*1000;
		long f = System.currentTimeMillis() -73*60*60*1000;
		long g = System.currentTimeMillis() -97*60*60*1000;
		long h = System.currentTimeMillis() -100*60*60*1000;
		long j = System.currentTimeMillis() -118*60*60*1000;

		long v=System.currentTimeMillis() - (a+60*1000*60*24);
		System.out.println("v="+Math.max(v,0));

//		System.out.println(calcMoney(a,2000,9900,200));
//		System.out.println(calcMoney(b,2000,9900,200));
//		System.out.println(calcMoney(c,2000,9900,200));
//		System.out.println(calcMoney(d,2000,9900,200));
//		System.out.println(calcMoney(e,2000,9900,200));
//		System.out.println(calcMoney(f,2000,9900,200));
//		System.out.println(calcMoney(g,2000,9900,200));
//		System.out.println(calcMoney(h,2000,9900,200));
//		System.out.println(calcMoney(j,2000,9900,200));

		System.out.println(calcMoney(a,100,9900,60));
//		System.out.println(calcMoney(b,100,9900,60));
//		System.out.println(calcMoney(c,100,9900,60));
//		System.out.println(calcMoney(d,100,9900,60));
//		System.out.println(calcMoney(e,100,9900,60));
//		System.out.println(calcMoney(f,100,9900,60));
//		System.out.println(calcMoney(g,100,9900,60));
//		System.out.println(calcMoney(h,100,9900,60));
//		System.out.println(calcMoney(j,100,9900,60));
	}

}
