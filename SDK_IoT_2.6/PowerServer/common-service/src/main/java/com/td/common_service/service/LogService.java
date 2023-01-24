package com.td.common_service.service;

import com.alibaba.fastjson.JSONObject;
import com.td.common_service.mapper.packet.PowerBankPackInfo;

public interface LogService {

    /**
     * 添加机器通讯日志
     * @param deviceUuid 设备号
     * @param event
     * @param machineUuid 命令
     * @param bid
     * @return
     */
    void addFindbackLog(String deviceUuid, String event, String machineUuid, int bid, JSONObject msg);

    /**
     * 添加订单日志
     * @param deviceUuid    模块号
     * @param orderNo       订单编号
     * @param machineUuid   命令
     * @param opType        操作类型：1租借，2租借响应，3归还
     */
    void addOrderLog(String deviceUuid, String orderNo, String machineUuid, int opType, JSONObject msg);

    void addPowerBankLog(PowerBankPackInfo powerBankPackInfo);
}
