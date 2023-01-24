package com.td.common_service.mapper.packet;

import com.td.util.HexToBinaryUtils;
import lombok.Data;

@Data
public class PowerBankPackInfo {


    private String posInfo; //一个仓位

    private String posId ; //仓位id
    private String backRes ; //归还状态  00 没有归还， 01有归还
    private String posState; //代表充电宝正在充电，00 未充电
    private String powerAD; //电压值16制
    private Integer powerADInt;//电压值转10进制
    private String powerNo; //充电宝id
    private String temp; //温度

    public PowerBankPackInfo(final String posInfo){
         posId = ""+(Integer.parseInt(posInfo.substring(0, 2))); //仓位id
         backRes = posInfo.substring(2, 4); //归还状态  00 没有归还， 01有归还
         posState = posInfo.substring(4, 6); //代表充电宝正在充电，00 未充电
         powerAD = posInfo.substring(6, 10); //电压值16制
         powerADInt = HexToBinaryUtils.getDecimal(powerAD);//电压值转10进制
         powerNo = posInfo.substring(10,20); //充电宝id
         temp = posInfo.substring(20, 22); //温度
    }

}
