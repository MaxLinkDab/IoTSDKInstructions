package com.td.util;

import static org.springframework.util.StringUtils.getFilename;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.td.util.fastDFS.FastDFSException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.poi.util.IOUtils;
import org.csource.fastdfs.ProtoCommon;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.td.util.fastDFS.FastDFSClient;

public class QRcodeUtils {


    /** 获取访问服务器的token，拼接到地址后面
     *
     * @param filepath 文件路径 group1/M00/00/00/wKgzgFnkTPyAIAUGAAEoRmXZPp876.jpeg
     * @param httpSecretKey 密钥
     * @return 返回token，如： token=078d370098b03e9020b82c829c205e1f&ts=1508141521
     */
    public static String getToken(String filepath, String httpSecretKey){
        // unix seconds
        int ts = (int) Instant.now().getEpochSecond();
        // token
        String token = "null";
        try {
            token = ProtoCommon.getToken(getFilename(filepath), ts, httpSecretKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("token=").append(token);
        sb.append("&ts=").append(ts);

        return sb.toString();
    }

//    public static void main(String[] args) throws Exception {
//
////        String accessToken = tokenService.getWxcToken("wx679d03dffa8469ce", "d23fd475e99eb8d7b4fe01b949e8746f");
////        String s = HttpClientUtils.sendGetData("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx679d03dffa8469ce&secret=d23fd475e99eb8d7b4fe01b949e8746f", "utf-8");
////        JSONObject jsonObject = JSON.parseObject(s);
////        jsonObject.get("access_token");
//
//        String accessToken = "21_d5jFo6Dn5PvQemNe3ZB3G0ZrjT_g2DnecLbTwKKP19mvKnxgsndnoJMmDqZThkLWIXM01DQoTIl_Uw-zoQYwJTuc-i80kA21GAFN6D7jnw8N-fVTphMGZas9cBJ72V52X8O1ggZZ-b8-xZW7JSVdAEAUDG";
//        String sceneStr = "7d8e5b4";
//        String uploadPath = getCodeM(accessToken, sceneStr);
//        System.out.println("路径： "+uploadPath); //group1/M00/00/00/rBLcLlzADYyAduvbAALKPsIlrAc513.jpg
//
    //    try {
    //        String url = "group1/M00/00/00/rBLcLlzBVlKAaCmTAALJ_tQDURc014.jpg";
    //        FastDFSClient.deleteFile(url);//删除FastDFS路径图片
    //    } catch (FastDFSException e) {
    //        e.printStackTrace();
    //    }
//    }



    /**
     * 获取二维码
     */
    public static String getCodeM(String token, String sceneStr) throws Exception {
        String imei = CryptographyUtil.md5(sceneStr,"asdfghjkl");

        Map<String, Object> params = new HashMap<>();
        params.put("scene", sceneStr);  //参数
        params.put("width", 800);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpPost httpPost = new HttpPost("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + token);  // 接口
        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
        String body = JSON.toJSONString(params);           //必须是json模式的 post
        StringEntity entity;
        entity = new StringEntity(body);
        entity.setContentType("image/png");

        httpPost.setEntity(entity);
        HttpResponse response;

        response = httpClient.execute(httpPost);
        InputStream inputStream = response.getEntity().getContent();
//        String result = EntityUtils.toString(response.getEntity(), "utf-8");
//        System.out.println("返回结果： "+result);
        String name = imei + ".jpg";
        System.out.println("文件名： "+name);
        String uploadPath = FastDFSClient.upload(inputStream,name,null);
//        saveToImgByInputStream(inputStream, "D:\\", name);  //保存图片

        return uploadPath;
    }





    public static File getTemplateFile(InputStream inputStream) {
        File file = null;
        try {
            file = File.createTempFile("temp_image", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStreamToFile(inputStream, file);
        if (file.exists() && file.length() <= 0) {
            return null;
        }
        return file;
    }

    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            try {
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            } finally {
                if (os != null) {
                    os.close();
                }
                if (ins != null) {
                    ins.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * File 转 MultipartFile
     * @param file
     * @throws Exception
     */
    public static MultipartFile fileToMultipartFile( File file ) throws Exception {

        FileInputStream fileInput = new FileInputStream(file);
        MultipartFile toMultipartFile = new MockMultipartFile("file",file.getName(),"text/plain", IOUtils.toByteArray(fileInput));
//        toMultipartFile.getInputStream();
        return toMultipartFile;
    }

    /**
     * 将二进制转换成文件保存
     *
     * @param instreams 二进制流
     * @param imgPath   图片的保存路径
     * @param imgName   图片的名称
     * @return 1：保存正常
     * 0：保存失败
     */
    public static MultipartFile saveToImgByInputStream(InputStream instreams, String imgPath, String imgName) {
        int stateInt = 1;
        File file = null;
        if (instreams != null) {
            try {
                file = new File(imgPath, imgName);//可以是任何图片格式.jpg,.png等
                FileOutputStream fos = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int nRead = 0;
                while ((nRead = instreams.read(b)) != -1) {
                    fos.write(b, 0, nRead);
                }
                fos.flush();
//                fos.close();

                if(1 == stateInt){
                    FileInputStream fileInput = new FileInputStream(file);
                    MultipartFile toMultipartFile = new MockMultipartFile("file",file.getName(),"text/plain", IOUtils.toByteArray(fileInput));
                    return toMultipartFile;
                }
            } catch (Exception e) {
                stateInt = 0;
                e.printStackTrace();
            } finally {
            }
        }

        return null;
    }

    /**
     * 将二进制转换成文件保存
     *
     * @param instreams 二进制流
     * @param imgPath   图片的保存路径
     * @param imgName   图片的名称
     * @return 1：保存正常
     * 0：保存失败
     */
    public static MultipartFile saveToImgByInputStream2(InputStream instreams, String imgPath, String imgName) {
        int stateInt = 1;
        File file = null;
        if (instreams != null) {
            try {
                file = new File(imgPath, imgName);//可以是任何图片格式.jpg,.png等
                FileOutputStream fos = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int nRead = instreams.read(b);
                fos.flush();
//                fos.close();

                if(1 == stateInt){
                    FileInputStream fileInput = new FileInputStream(file);
                    MultipartFile toMultipartFile = new MockMultipartFile("file",file.getName(),"text/plain", IOUtils.toByteArray(fileInput));
                    return toMultipartFile;
                }
            } catch (Exception e) {
                stateInt = 0;
                e.printStackTrace();
            } finally {
            }
        }

        return null;
    }


    private static String valueOfString(String str, int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len - str.length(); i++) {
            sb.append("0");
        }
        return (sb.length() == 0) ? (str) : (sb.toString() + str);
    }

    public String getDateFormat(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    public static Date getDateFormat(String time){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getTimeString(Calendar calendar) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(calendar.get(Calendar.YEAR)))
                .append(valueOfString(String.valueOf(calendar.get(Calendar.MONTH) + 1),2))
                .append(valueOfString(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)),2))
                .append(valueOfString(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)),2))
                .append(valueOfString(String.valueOf(calendar.get(Calendar.MINUTE)),2))
                .append(valueOfString(String.valueOf(calendar.get(Calendar.SECOND)),2))
                .append(valueOfString(String.valueOf(calendar.get(Calendar.MILLISECOND)),3));
        return sb.toString();
    }

    public static String getTimeString(String time){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(getDateFormat(time));
        return getTimeString(calendar);
    }

    public static String getTimeString(){
        Calendar calendar = new GregorianCalendar();
        return getTimeString(calendar);
    }
}
