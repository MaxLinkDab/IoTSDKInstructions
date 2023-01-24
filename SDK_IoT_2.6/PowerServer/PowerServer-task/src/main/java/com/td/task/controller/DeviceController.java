package com.td.task.controller;


import com.td.common.vo.R;
import com.td.common_service.model.DeviceInfo;
import com.td.common_service.model.OrderRentPay;
import com.td.common_service.model.Powerbank;
import com.td.common_service.model.WxaUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.td.common_service.service.DeviceService;

import java.util.List;

/**
 * 管理设备端
 */
//@Controller
//@RequestMapping("/device")
public class DeviceController {
    private static final Logger log = LoggerFactory.getLogger(DeviceController.class);
    @Autowired
    private DeviceService deviceService;

    /**
     * 设备上报信息接口
     * @param requestJson
     * @return
     */
    @RequestMapping(value = "/back", method = RequestMethod.POST)
    @ResponseBody
    public int backReport(@RequestBody String requestJson) {

        log.info("backReport====收到具体参数requestJson :{}", requestJson);
        String[] splitData = requestJson.split(",");
        return deviceService.backReport(splitData, requestJson);

    }

    /**
     * scanner the q-code and start to rent
     * @param wxaUser the user information
     * @param uuid the device uuid
     */
    @RequestMapping(value = "/startRent", method = RequestMethod.POST)
    @ResponseBody
    R startRent(String wxaUser, String uuid){
        log.info("startRent====param:wxaUser :{},====参数uuid:{}", wxaUser.toString(),uuid);
//        return deviceService.startRent(wxaUser, uuid);
        return null;
    }

    /**
     *  retrieve  device information by device uuid
     * @param uuid the device uuid
     * @return
     */
    @RequestMapping(value = "/getDeviceInfoByUuid", method = RequestMethod.GET)
    @ResponseBody
    DeviceInfo getDeviceInfoByUuid(String uuid){
        log.info("getDeviceInfoByUuid====param:uuid:{}", uuid);
        return deviceService.getDeviceInfoByUuid(uuid);
    }

    /**
     * modify the device state
     * @param deviceUuid the device uuid
     * @param deviceState the device state
     * @param ip the device ip address
     * @return
     */
    int updateState(String deviceUuid, Integer deviceState,String ip){
        log.info("updateState====param:uuid:{},deviceState:{},ip;{}", deviceUuid,deviceState,ip);
        return deviceService.updateState(deviceUuid,deviceState,ip);
    }


    /**
     * lease test; it is only use to test tools
     * @param uuId the device uuid
     * @param machineId the mcu No; a device include one or more mcu
     * @param positionNo the postion of warehouse
     * @return
     */
    Boolean leaseIssue(String uuId, String machineId, String positionNo){
        log.info("leaseIssue====param:uuid:{},machineId:{},ip;{}", uuId,machineId,positionNo);
        return deviceService.leaseIssue(uuId,machineId,positionNo);
    }


    /**
     * set all netty device to offline,noto mqtt device
     */
    void updateNettyDeviceToOffline(){
        log.info("updateNettyDeviceToOffline====");
        deviceService.updateNettyDeviceToOffline();
    }


    /**
     * retrieve the devices through device version information
     * @param versionInfo the query Conditions
     * @return
     */
    List<DeviceInfo> selectDeviceInfoByHardVersion(String versionInfo){
        log.info("selectDeviceInfoByHardVersion====param:versionInfo:{}", versionInfo);
        return deviceService.selectDeviceInfoByHardVersion(versionInfo);
    }

    /**
     * retrieve the devices through protocol version information
     * @param versionInfo the query Conditions
     * @return
     */
    List<DeviceInfo> selectDeviceInfoByProtocolVersion(String versionInfo){
        log.info("selectDeviceInfoByProtocolVersion====param:versionInfo:{}", versionInfo);
        return deviceService.selectDeviceInfoByProtocolVersion(versionInfo);
    }


    /**
     * add a  new devices information
     * @param deviceInfo the new information
     * @return
     */
    int addDeviceInfo(DeviceInfo deviceInfo){
        log.info("addDeviceInfo====param:deviceInfo:{}", deviceInfo.toString());
        return deviceService.addDeviceInfo(deviceInfo);
    }

    /**
     * delete a device
     * @param deviceUuid device uuid
     * @return
     */
    int delDeviceInfo(String deviceUuid){
        log.info("delDeviceInfo====param:deviceUuid:{}", deviceUuid);
        return deviceService.delDeviceInfo(deviceUuid);
    }

    /**
     * edit a devices information
     * @param deviceInfo device information
     * @return
     */
    int editDeviceInfo(DeviceInfo deviceInfo){
        log.info("editDeviceInfo====param:deviceInfo:{}", deviceInfo.toString());
        return deviceService.editDeviceInfo(deviceInfo);
    }


    /**
     * get warehouse information  by a rent order information
     * @param orderNo rent order number
     * @return
     */
    R getPowerbankDetail(String orderNo, String deviceUuid){
        log.info("getPowerbankDetail====param:orderNo:{}", orderNo);
        return deviceService.getPowerbankDetail(orderNo, deviceUuid);
    }


    /**
     * start to rent a charge ,it is only used to test tools
     * @param uuid  the device uuid
     * @return
     */
    R sendMsg(String uuid, String positionId){
        log.info("sendMsg====param:deviceUuid:{}", uuid);
        return deviceService.sendMsg(uuid, positionId);
    }


    /**
     * retrieve a warehouse information
     * @param positionUuid  the postion uuid
     * @param machineUuid  the machine uuid
     * @param deviceUuid  the device uuid
     * @return
     */
    Powerbank getPowerbank(String positionUuid, String machineUuid, String deviceUuid){
        log.info("getPowerbank====param:deviceUuid:{},machineUuid:{},positionUuid:{}", deviceUuid,machineUuid,positionUuid);
        return deviceService.getPowerbank(deviceUuid,machineUuid,positionUuid);
    }

    /**
     * retrieve all warehouse information by device uuid
     * @param deviceUuid  the device uuid
     * @return
     */
    List<Powerbank> getPowerbanks(String deviceUuid){
        log.info("getPowerbanks====param:deviceUuid:{}", deviceUuid);
        return deviceService.getPowerbanks(deviceUuid);
    }

    /**
     * get mqtt device's state , online or offline
     * @param deviceUuid  the device uuid
     * @return
     */
    boolean getDeviceState(String deviceUuid){
        log.info("getDeviceState====param:deviceUuid:{}", deviceUuid);
        return deviceService.getDeviceState(deviceUuid);
    }

    /**
     * Forced change the all warehouse's state
     * @param deviceUuid  the device uuid
     * @return
     */
    int alterAllPowerBackState(String deviceUuid){
        log.info("alterPowerState====param:deviceUuid:{}", deviceUuid);
        return deviceService.alterPowerState(deviceUuid);
    }

}
