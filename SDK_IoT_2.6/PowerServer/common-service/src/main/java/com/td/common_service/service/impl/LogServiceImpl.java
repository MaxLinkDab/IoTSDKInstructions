package com.td.common_service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.td.common_service.mapper.FindbackLogMapper;
import com.td.common_service.mapper.OrderLogMapper;
import com.td.common_service.mapper.PowerbankLogMapper;
import com.td.common_service.mapper.packet.PowerBankPackInfo;
import com.td.common_service.model.FindbackLog;
import com.td.common_service.model.OrderLog;
import com.td.common_service.model.PowerbankLog;
import com.td.common_service.service.LogService;
import com.td.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private FindbackLogMapper findbackLogMapper;
    @Autowired
    private OrderLogMapper orderLogMapper;
    @Autowired
    private PowerbankLogMapper powerbankLogMapper;

    @Async
    @Override
    @Transactional
    public void addFindbackLog(String deviceUuid, String event, String machineUuid, int bid, JSONObject msg) {
        FindbackLog findbackLog = new FindbackLog();
        findbackLog.setBid(bid);
        findbackLog.setCreatedTime(new Date(System.currentTimeMillis()));
        findbackLog.setDeviceUuid(deviceUuid);
        findbackLog.setEvent(event);
        findbackLog.setMachineUuid(machineUuid);
        Object receiveTime = msg.get("generateTime");
        Object execTime = msg.get("execTime");
        if (receiveTime != null) {
            findbackLog.setReceiveTime(new Date((Long) receiveTime));
            findbackLog.setIotMessage(msg.toJSONString());
        }
        if (execTime != null) {
            findbackLog.setExecTime(new Date((Long) execTime));
        }
        findbackLogMapper.insertSelective(findbackLog);
    }

    @Async
    @Override
    @Transactional
    public void addOrderLog(String deviceUuid, String orderNo, String machineUuid, int opType, JSONObject msg) {
        if (StringUtils.isEmpty(orderNo)) {
            return;
        }
        OrderLog orderLog = new OrderLog();
        orderLog.setOpType(opType);
        orderLog.setOrderNo(orderNo);
        orderLog.setDeviceUuid(deviceUuid);
        orderLog.setContent(machineUuid);
        Object receiveTime = msg.get("generateTime");
        Object execTime = msg.get("execTime");
        if (receiveTime != null) {
            orderLog.setReceiveTime(new Date((Long) receiveTime));
            orderLog.setIotMessage(msg.toJSONString());
        }
        if (execTime != null) {
            orderLog.setExecTime(new Date((Long) execTime));
        }
        orderLogMapper.insertSelective(orderLog);
    }

    @Async
    @Override
    @Transactional
    public void addPowerBankLog(PowerBankPackInfo powerBankPackInfo) {
        PowerbankLog powerbankLog = new PowerbankLog();
        powerbankLog.parsePowerbankPacket(powerBankPackInfo);
        powerbankLogMapper.insertSelective(powerbankLog);
    }
}
