package com.td.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.td.util.config.AliyunConfig;

/**
 * 签名工具主入口
 */
public class AliyunSign {

    // 1.需求修改Config.java中的AccessKey信息
    // 2.建议使用方法二，所有参数都需要一一填写
    // 3."最终signature"才是你需要的签名最终结果
    public static void main(String[] args) throws UnsupportedEncodingException {

//        // 方法一
//        System.out.println("方法一：");
//        String str = "GET&%2F&AccessKeyId%3D" + AliyunConfig.accessKey
//                + "%26Action%3DRegisterDevice%26DeviceName%3D1533023037%26Format%3DJSON%26ProductKey%3DaxxxUtgaRLB%26RegionId%3Dcn-shanghai%26SignatureMethod%3DHMAC-SHA1%26SignatureNonce%3D1533023037%26SignatureVersion%3D1.0%26Timestamp%3D2018-07-31T07%253A43%253A57Z%26Version%3D2018-01-20";
//        byte[] signBytes;
//        try {
//            signBytes = SignatureUtils.hmacSHA1Signature(AliyunConfig.accessKeySecret + "&", str.toString());
//            String signature = SignatureUtils.newStringByBase64(signBytes);
//            System.out.println("signString---" + str);
//            System.out.println("signature----" + signature);
//            System.out.println("最终signature：" + URLEncoder.encode(signature, AliyunConfig.CHARSET_UTF8));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println();

        // 方法二
//        System.out.println("方法二：");
//        Map<String, String> map = new HashMap<String, String>();
//        // 公共参数
//        map.put("Format", "JSON");
//        map.put("Version", "2018-01-20");
//        map.put("AccessKeyId", AliyunConfig.ACCESS_KEY_ID);
//        map.put("SignatureMethod", "HMAC-SHA1");
//        map.put("Timestamp", "2018-07-31T07:43:57Z");
//        map.put("SignatureVersion", "1.0");
//        map.put("SignatureNonce", "1533023037");
//        map.put("RegionId", "cn-shanghai");
//        // 请求参数
//        map.put("Action", "RegisterDeviceReq");
//        map.put("DeviceName", "1533023037");
//        map.put("ProductKey", "axxxUtgaRLB");
//        try {
//            String signature = SignatureUtils.generate("GET", map, AliyunConfig.ACCESS_KEY_SECRET);
//            System.out.println("最终signature：" + signature);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println();
        Map<String, String> map = new HashMap<String, String>();
        map.put("Action", "GetDeviceStatus");
        map.put("DeviceName", "4d65c16");
        map.put("ProductKey", AliyunConfig.PRODUCT_KEY);
        System.out.println(getSignature("12041553131",map,""));


    }

    /**
     * 阿里云签名(国内)
     * @param nonce
     * @param stringMap
     * @param date
     * @return
     */
    public static String getSignature(String nonce,Map<String,String> stringMap,String date){
        Map<String, String> map = new HashMap<String, String>();
        // 公共参数
        map.put("Format", "JSON");
        map.put("Version", "2018-01-20");
        map.put("AccessKeyId", AliyunConfig.ACCESS_KEY_ID);
        map.put("SignatureMethod", "HMAC-SHA1");
        map.put("SignatureVersion", "1.0");
        map.put("Timestamp", date);
        map.put("SignatureNonce", nonce);
        map.put("RegionId", AliyunConfig.REGION_ID);
        // 请求参数
        map.putAll(stringMap);
        System.out.println(map);
        try {
            String signature = SignatureUtils.generate("GET", map, AliyunConfig.ACCESS_KEY_SECRET);
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 阿里云签名(国外)
     * @param nonce
     * @param stringMap
     * @param date
     * @return
     */
    /*public static String getSignature(String nonce,Map<String,String> stringMap,String date){
        Map<String, String> map = new HashMap<String, String>();
        // 公共参数
        map.put("Format", "JSON");
        map.put("Version", "2018-01-20");
        map.put("AccessKeyId", AliyunConfig.ACCESS_KEY_ID);
        map.put("SignatureMethod", "HMAC-SHA1");
        map.put("SignatureVersion", "1.0");
        map.put("Timestamp", date);
        map.put("SignatureNonce", nonce);
        map.put("RegionId", "us-west-1");
        // 请求参数
        map.putAll(stringMap);
        System.out.println(map);
        try {
            String signature = SignatureUtils.generate("GET", map, AliyunConfig.ACCESS_KEY_SECRET);
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }*/

    /**
     * 国内
     * @param map
     * @return
     */
    public static String getUrl(Map<String,String> map){
        String url = "https://"+ AliyunConfig.DOMAIN +"/?";
        // 公共参数
        map.put("Format", "JSON");
        map.put("Version", "2018-01-20");
        map.put("AccessKeyId", AliyunConfig.ACCESS_KEY_ID);
        map.put("SignatureMethod", "HMAC-SHA1");
        map.put("SignatureVersion", "1.0");
        map.put("RegionId", AliyunConfig.REGION_ID);
        Iterator it = map.entrySet().iterator() ;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next() ;
            Object key = entry.getKey();
            Object value = entry.getValue();
            url = url +key+"="+value+"&";
        }
        return url.substring(0,url.length()-1);
    }
    /*public static String getUrl(Map<String,String> map){
        //String url = "https://iot.cn-shanghai.aliyuncs.com/?";
        //String url = "https://iot.cn-us-west-1.aliyuncs.com/?";
        String url = "https://iot.us-west-1.aliyuncs.com/?";
        // 公共参数
        map.put("Format", "JSON");
        map.put("Version", "2018-01-20");
        map.put("AccessKeyId", AliyunConfig.ACCESS_KEY_ID);
        map.put("SignatureMethod", "HMAC-SHA1");
        map.put("SignatureVersion", "1.0");
        map.put("RegionId", "us-west-1");
        //map.put("RegionId", "cn-shanghai");
        Iterator it = map.entrySet().iterator() ;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next() ;
            Object key = entry.getKey();
            Object value = entry.getValue();
            url = url +key+"="+value+"&";
        }
        return url.substring(0,url.length()-1);
    }*/

    /**
     * 获取iso
     * @param date 标准时间
     * @return
     */
    public static String getISOTime(Date date){
        //data转化成ISO8601格式时间
        TimeZone tz = TimeZone.getTimeZone("WAT");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        System.out.println(nowAsISO);
        return nowAsISO;
    }
}
