package com.td.util;

import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

/**
 * @Description: 公共工具类
 * @Author: chenqp
 * @CreateDate: 2019/3/27 15:22
 * @Version: 1.0
 */
public class Base64Utils {

    /**
     * Base64编码(依赖sun.misc.BASE64Decoder.jar)
     *
     * @param data 要加密的字符数组
     * @return String 加密后的16进制字符串
     */
    public static String encode(byte[] data) {
        return new BASE64Encoder().encode(data);
    }


    /**
     * Base64解码(依赖sun.misc.BASE64Decoder.jar)
     *
     * @param data 要解密的字符串
     * @return String 解密后的字符串
     * @throws IOException
     */
    public static String decode(String data) throws IOException {
        return new String(new BASE64Decoder().decodeBuffer(data));
    }

    /**
     * Base64编码(JDK1.8以后才能使用)
     *
     * @param data 要加密的字符数组
     * @return String 加密后的16进制字符串
     */
    public static String encode_JDK18(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Base64解码(JDK1.8以后才能使用)
     *
     * @param data 要解密的字符串
     * @return String 解密后的字符串
     * @throws IOException
     */
    public static String decode_JDK18(String data) throws IOException {
        return new String(Base64.getDecoder().decode(data));
    }

    /**
     * main方法进行测试
     *
     * @param args
     * @throws IOException
     * @throws Exception
     */
//    public static void main(String[] args) throws IOException {
//        String username = "admin";
//        String password = "admin";
//        String encryptMessage = username + ":" + password;
//        //编码(依赖sun.misc.BASE64Decoder.jar)
//     //   System.out.println(encode(encryptMessage.getBytes()));//YWRtaW46YWRtaW4=
//        //解码(依赖sun.misc.BASE64Decoder.jar)
//        System.out.println(decode("NTUzMTAzMDEwMjAxOWE="));//admin:admin
//
//        //编码(JDK1.8以后才能使用)
//     //   System.out.println(encode_JDK18(encryptMessage.getBytes()));//YWRtaW46YWRtaW4=
//        //解码(JDK1.8以后才能使用)
//     //   System.out.println(decode_JDK18(encode(encryptMessage.getBytes())));//admin:admin
//    }

}
