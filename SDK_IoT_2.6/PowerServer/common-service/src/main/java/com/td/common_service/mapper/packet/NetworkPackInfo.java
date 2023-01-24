package com.td.common_service.mapper.packet;

import lombok.Data;

@Data
public class NetworkPackInfo {

    private String deviceUuid ;
    private String signalVersion;  //信号
    private String networkVersion ;  //网络制式
    private String operatorVersion ;  //运营商
    private String iccid;


    public NetworkPackInfo(String[] splitData) {
         deviceUuid = splitData[1]; //设备uuid
         signalVersion = splitData[2];  //信号
         networkVersion = splitData[3];  //网络制式
         operatorVersion = splitData[4];  //运营商
         iccid = splitData[5];  //运营商
    }
}
