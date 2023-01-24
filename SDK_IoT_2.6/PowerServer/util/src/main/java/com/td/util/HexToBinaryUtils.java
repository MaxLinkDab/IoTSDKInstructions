package com.td.util;

import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

import java.util.Base64;

/**

* @Description:    进制转换工具类
* @Author:       chen
* @CreateDate:     2019/4/8 17:12
* @Version:        1.0

*/
public class HexToBinaryUtils {


    /**
     * 获取param 字符串的校验码：用于与嵌入式通讯时校验
     * @param param
     * @return
     */
    public static String getCheckCode(String param) {
        String endString = "";
        int hexNu = Integer.valueOf(yiHuo(param), 16);// 得到16进制
        String size = Integer.toBinaryString(hexNu);// 转换为二进制
        // 判断是否为8位二进制，否则左补零
        if (size.length() != 8) {
            for (int i = size.length(); i < 8; i++) {
                size = "0" + size;
            }
        }
        /**
         * 得到二进制的字符串，遍历该字符串，进行取反操作
         */
        char[] icha = size.toCharArray();
        for (int i = 0; i < icha.length; i++) {
            if ((icha[i] + "").equals("0")) {
                endString += 1;
            }
            if ((icha[i] + "").equals("1")) {
                endString += 0;
            }
        }
        return binaryString2hexString(endString);// 返回把二进制字符串转为十六进制结果集
    }

    /**
     * 将十六进制字符串转为十六进制,并进行base64加密 (主动下发时，使用)
     *
     * @param param
     * @return
     */
    public static String getHexString(String param) {

        if (StringUtils.isEmpty(param)) {
            return null;
        }
        char[] params = new char[param.length()/2];

        for (int i = 0; i < param.length()/2; i++) {
            String sub = param.substring(i * 2, (i + 1) * 2);
            Integer decimal = getDecimal(sub);
            params[i] = (char)decimal.intValue();
        }
//        char[] params = {0x55, 0x31, 0x03, 0x01, 0x02, 0x01, 0x9a};
        String pparam = "";
        try {
            byte[] endParam = Base64.getEncoder().encode(new String(params).getBytes("iso-8859-1"));
            System.out.println("********************   333 >" + endParam.toString());
            pparam = new String(endParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pparam;
    }


    /**
     * 将10进制数值转为16进制
     *
     * @param param
     * @return
     */
    public static String getHex(String param) {
        int num = Integer.valueOf(param);
        String hex = Integer.toHexString(num);
        return hex.length()%2==0?hex:"0"+hex;
    }

    /**
     * 将16进制数值转为10进制
     *
     * @param param
     * @return
     */
    public static Integer getDecimal(String param) {

        return Integer.parseInt(param, 16);
    }


    public static String change(String content) {
        String str = "";
        for (int i = 0; i < content.length(); i++) {
            if (i % 2 == 0) {
                str += " " + content.substring(i, i + 1);
            } else {
                str += content.substring(i, i + 1);
            }
        }
        return str.trim();
    }

    /**
     * 进行异或运算
     *
     * @param content
     * @return
     */
    private static String yiHuo(String content) {
        content = change(content);
        String[] b = content.split(" ");
        int a = 0;
        for (int i = 0; i < b.length; i++) {
            a = a ^ Integer.parseInt(b[i], 16);
        }
        if (a < 10) {
            StringBuffer sb = new StringBuffer();
            sb.append("0");
            sb.append(a);
            return sb.toString();
        }
        return Integer.toHexString(a);
    }

    /**
     * 字符串转为二进制
     *
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            return null;
        }
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * 讲字符串转为16进制
     *
     * @param bString
     * @return
     */
    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * ascii转String
     * @param value 2位数
     * @return
     */
    public static String asciiToString(String value)
    {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    /**
     * byte转无符号
     * @param data
     * @return
     */
    public static int getUnsignedByte (byte data){ //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data&0x0FF ;
    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     *  byte数组转int
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static void main(String[] args) {
        //int money = UtilTool.calcMoney(System.currentTimeMillis()-(1000*60*59/*h*/),2000,9900,200);
        //System.out.println("money: "+money);

/*        byte[] encode = new byte[]{0x55,0x33,0x02,0x01,0x02, (byte) 0x9b};
        System.out.println("553501019F");
        System.out.println(HexToBinaryUtils.getHexString("55310301010099"));
        encode = new byte[]{55, 33, 02, 01, 02, 98};
        String cmd = "5533020102";
        cmd = cmd+getCheckCode(cmd);
        cmd=HexToBinaryUtils.getHexString(cmd);
        System.out.println("cmd: "+cmd);
        byte[] decode = Base64.getMimeDecoder().decode("VTYuAQEBAQAAD7WiAAV9AAIBAAAPtaIABZQABQEAAAS0ogAFowAGAQAAA7SiAAWOAHE=");//VTMCAQKY
        //byte[] decode = Base64.getMimeDecoder().decode("VTEDAQIBmg==");
        String s = HexToBinaryUtils.bytesToHexString(decode);
        String change = HexToBinaryUtils.change(s);
        System.out.println(s + " ррр " +change);

        String aa = "55-36-44-02-00-01-00-00-00-0f-c5-a2-13-0d-ba-00-02-00-00-00-0f-c5-a2-13-0d-c2-00-03-00-00-00-0f-c5-a2-13-0d-ae-00-04-00-00-00-0f-c5-a2-13-0d-8f-00-05-00-00-00-0f-c5-a2-13-0d-c3-00-06-00-00-00-0f-c5-a2-13-0d-c5-00-82";
        aa = aa.replaceAll("-","");
        System.out.println(aa);
        String bb = "55365A0101010000000FC6A210C36D00020000000FC6A210C35100030000000FC6A210C37900040000000FC6A210C35D00050000000FC6A210C36300060100000FC6A210C36200070100000FC6A210C37500080100000FC6A210C36700C4";
        System.out.println(HexToBinaryUtils.getHexString(bb));

        String cc = "55320C01030100200007C2A2130F5E92";
        System.out.println(HexToBinaryUtils.getHexString(cc));*/
    }
}
