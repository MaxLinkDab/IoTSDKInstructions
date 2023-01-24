package com.td.common_service.message.device;


import com.td.common.vo.RequestMessage;

import lombok.Data;

/**

* @Description:  激活设备请求实体
* @Author:       chen
* @CreateDate:     2019/3/28 15:07
* @Version:        1.0

*/
@Data
public class ActivationDeviceRequest extends RequestMessage{

    /**
     * 设备cloudId
     */
    private String devId;
    /**
     * sim卡卡号
     */
    private String iccId;
    /**
     * 签名
     */
    private String signature;

    /**
     *模块sn号
     */
    private String sn;
    /**
     * 事件id
     */
    private long trace;

    /**
     * 设备仓位数
     */
    private Integer spaceNu;

    /**
     * 设备电压值
     */
    private Integer voltageNu;



}