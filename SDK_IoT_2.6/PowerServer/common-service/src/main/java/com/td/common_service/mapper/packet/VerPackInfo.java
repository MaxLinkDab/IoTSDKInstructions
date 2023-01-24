package com.td.common_service.mapper.packet;

import lombok.Data;

import java.io.Serializable;

@Data
public class VerPackInfo implements Serializable {

    private String deviceUuid ; //设备uuid
    private String softVersion;  //软件版本
    private String hardVersion ;  //硬件版本
    private String agreementVersion;  //协议版本
    private String deviceModel="0";  //设备类型

    public VerPackInfo(String[] splitData) {
        deviceUuid = splitData[1]; //设备uuid
        softVersion = splitData[2];  //软件版本
        hardVersion = splitData[3];  //硬件版本
        if (splitData.length>=5){
            agreementVersion = splitData[4];  //协议版本
        }
        if (splitData.length>=6){
            deviceModel = splitData[5];  //设备类型
        }
    }

}
