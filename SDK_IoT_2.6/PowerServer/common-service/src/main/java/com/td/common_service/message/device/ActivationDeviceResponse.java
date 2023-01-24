package com.td.common_service.message.device;


import com.td.common.vo.ResponseMessage;

import lombok.Data;

/**

* @Description:  激活设备响应实体
* @Author:       chen
* @CreateDate:     2019/3/28 15:07
* @Version:        1.0

*/
@Data
public class ActivationDeviceResponse extends ResponseMessage {


    /**
     * 返回设备key，用户全局访问
     */
    private String deviceKey;


}