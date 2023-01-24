package com.td.api.controller;

import com.td.api.config.ApiJsonObject;
import com.td.api.config.ApiJsonProperty;
import com.td.common.utils.RUtil;
import com.td.common.vo.R;
import com.td.common_service.model.DeviceInfo;
import com.td.common_service.service.DeviceService;
import com.td.common_service.service.jedis.RedisService;
import com.td.util.StringUtils;
import com.td.util.TestQRcode;
import com.td.util.UtilTool;
import com.td.util.device.DeviceUtil;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : yangchangsong
 * @version : V1.0
 * @since : 2019/07/01
 */
@Slf4j
@RestController
@RequestMapping("device")
@Api(value = "DeviceController", description = "device information and control")
//@SecurityRequirement(name = "javainuseapi")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;


    /**
     * open warehouse test; it is only use to test tools
     * @param map the device uuid
     * @param map the mcu No; a device include one or more mcu
     * @param map the postion of warehouse
     * @return
     */
    @ApiOperation(value = "rent")
    @ResponseBody
    @PostMapping("/rent")
    R rent(@ApiJsonObject(name = "rent_uuid_machineId_pos)", value = {
            @ApiJsonProperty(key = "deviceUuid", example = "865860042609660", description = "device uuid"),
            @ApiJsonProperty(key = "machineId", example = "01", description = "machine id"),
            @ApiJsonProperty(key = "positionId", example = "1", description = "position id")
    }) @RequestBody Map<String, Object> map){
        if(map.get("deviceUuid")==null){
            return RUtil.error("the device uuid is required!");
        }
        if(map.get("machineId")==null){
            return RUtil.error("the machineId is required!");
        }
        if(map.get("positionId")==null){
            return RUtil.error("the positionId is required!");
        }
        String execTime = null;
        if(map.get("execTime")==null){
            Long time = new Date().getTime() / 1000;
            execTime = String.valueOf(time);
        }
        String uuid = (String) map.get("deviceUuid");
        String machineId = (String) map.get("machineId");
        String positionNo = (String) map.get("positionId");
        log.info("openWarehouse====param:uuid:{},machineId:{},ip;{}", uuid,machineId,positionNo);
        Boolean aBoolean = deviceService.leaseIssue(execTime,uuid, machineId, positionNo);
        if(aBoolean){
            return RUtil.success(aBoolean);
        }else{
            return RUtil.error("the position is no powerbank");
        }

    }

/*{
    "deviceUuid": "862506049385829",
    "machineId": "01",
    "positionId": "1"
}*/
    /**
     * open warehouse test; it is only use to test tools
     * @param map the device uuid
     * @param map the mcu No; a device include one or more mcu
     * @param map the postion of warehouse
     * @return
     */
    @ApiOperation(value = "open warehouse")
    @ResponseBody
    @PostMapping("/openWarehouse")
    R openWarehouse(@ApiJsonObject(name = "openWarehouse_uuid_machineId_pos)", value = {
            @ApiJsonProperty(key = "deviceUuid", example = "862506049385829", description = "device uuid"),
            @ApiJsonProperty(key = "machineId", example = "01", description = "machine id"),
            @ApiJsonProperty(key = "positionId", example = "1", description = "position id")
    }) @RequestBody Map<String, Object> map){
        if(map.get("deviceUuid")==null){
            return RUtil.error("the device uuid is required!");
        }
        if(map.get("machineId")==null){
            return RUtil.error("the machineId is required!");
        }
        if(map.get("positionId")==null){
            return RUtil.error("the positionId is required!");
        }
        String execTime = null;
        if(map.get("execTime")==null){
            Long time = new Date().getTime() / 1000;
            execTime = String.valueOf(time);
        }else{
            execTime = (String)map.get("execTime");
        }
        String uuid = (String) map.get("deviceUuid");
        String machineId = (String) map.get("machineId");
        String positionNo = (String) map.get("positionId");
        log.info("openWarehouse====param:uuid:{},machineId:{},ip;{}", uuid,machineId,positionNo);
        Boolean aBoolean = deviceService.leaseIssue(execTime,uuid, machineId, positionNo);
        if(aBoolean){
            return RUtil.success(aBoolean);
        }else{
            return RUtil.error("the position is no powerbank");
        }

    }
/*
    @ApiOperation(value = "open warehouse")
    @ResponseBody
    @GetMapping("/showStations")
    String test() {
        return deviceInfoMapper.selectAll().stream().map(deviceInfo -> deviceInfo.getDeviceName() + deviceInfo.getPowerbankList().size()+"/"+deviceInfo.getSpaceNu()).collect(Collectors.joining("\n"));
    }

    @ApiOperation(value = "open warehouse")
    @ResponseBody
    @GetMapping("/t")
    String test(@ApiParam(name="tt") @RequestParam String firstName){
        return firstName;
    }*/

    /**
     *  retrieve  device information by device uuid
     * @param map the device uuid
     * @return
     */
    @ApiOperation(value = "retrieve  device information by device uuid")
    @ResponseBody
    @PostMapping("/getDeviceInfoByUuid")
    R getDeviceInfoByUuid(@ApiJsonObject(name = "getDeviceInfo_uuid", value = {
            @ApiJsonProperty(key = "deviceUuid", example = "865860042609660", description = "device uuid")
    }) @RequestBody Map<String, Object> map){
        String uuid = (String) map.get("deviceUuid");
        log.info("getDeviceInfoByUuid====param:uuid:{}", uuid);
        //deviceService.updatePowerbank(uuid,"01");
        return RUtil.success(deviceService.getDeviceInfoByUuid(uuid));
    }

    /**
     * set callback url to device by uuid
     * @param map device uuid
     * @return
     */
    @ApiOperation(value = "set callback url to device")
    @ResponseBody
    @PostMapping("/setCallbackUrl")
    R setCallbackUrl(@ApiJsonObject(name = "setCallbackUrl_uuid_url", value = {
            @ApiJsonProperty(key = "deviceUuid", example = "865860042609660", description = "device uuid"),
            @ApiJsonProperty(key = "url", example = "www.xxx.com:3322/callback", description = "callback url")
    }) @RequestBody Map<String, Object> map){
        if(map.get("deviceUuid")==null){
            return RUtil.error("the device uuid is required!");
        }
        if(map.get("url")==null){
            return RUtil.error("the url is required!");
        }
        String uuid = (String) map.get("deviceUuid");
        String url = (String) map.get("url");
        if(map.get("deviceUuid")==null){
            return RUtil.error("the device uuid is required!");
        }
        DeviceInfo deviceInfo = deviceService.getDeviceInfoByUuid(uuid);
        if(deviceInfo==null){
            return  RUtil.error("device isn't exsit");
        }
        deviceInfo.setUrl(url);
        log.info("deviceUuid====param:deviceUuid:{},url:{}", uuid,url);
        return RUtil.success(deviceService.editDeviceInfo(deviceInfo));
    }

    @ApiOperation(value = "upgrade")
    @ResponseBody
    @PostMapping("/upgrade")
    R upgrade(@ApiJsonObject(name = "upgrade ", value = {
            @ApiJsonProperty(key = "deviceUuid", example = "865860042609660", description = "device uuid"),
            @ApiJsonProperty(key = "type", example = "dtu", description = "dtu:主机 slave:从机"),
            @ApiJsonProperty(key = "filename", example = "WE20-6b6fee-MSV146.bin", description = "补丁文件名称")
    }) @RequestBody Map<String, Object> map) {
//        主机升级，类型就是dtu，从机升级，类型就是slave
//        deviceService.
        String type = String.valueOf(map.get("type"));
        String deviceUuid = String.valueOf(map.get("deviceUuid"));
        String filename = String.valueOf(map.get("filename"));
        boolean upgrade = deviceService.upgrade(deviceUuid, type, filename);
        return RUtil.success(upgrade);
    }

    @ApiOperation(value = "uploadLog")
    @ResponseBody
    @PostMapping("/uploadLog")
    R uploadLog(@ApiJsonObject(name = "uploadLog ", value = {
            @ApiJsonProperty(key = "deviceUuid", example = "865860042609660", description = "device uuid"),
            @ApiJsonProperty(key = "ip", example = "10.10.10.10", description = "服务器ip"),
            @ApiJsonProperty(key = "username", example = "admin", description = "服务器用户名"),
            @ApiJsonProperty(key = "password", example = "password", description = "服务器密码")
    }) @RequestBody Map<String, Object> map) {
        String deviceUuid = String.valueOf(map.get("deviceUuid"));
        String ip = String.valueOf(map.get("ip"));
        String username = String.valueOf(map.get("username"));
        String password = String.valueOf(map.get("password"));
        boolean result = deviceService.uploadLog(deviceUuid, ip, username, password);
        return RUtil.success(result);
    }

    ///**
    // *  retrieve  device information by device uuid
    // * @param map the device uuid
    // * @return
    // */
    //@ApiOperation(value = "retrieve  device information by device uuid")
    //@ResponseBody
    //@PostMapping("/callback")
    //R callback(@ApiJsonObject(name = "callback", value = {
    //        @ApiJsonProperty(key = "data", example = "", description = "")
    //}) @RequestBody Map<String, Object> map){
    //    //String uuid = (String) map.get("deviceUuid");
    //    //log.info("getDeviceInfoByUuid====param:uuid:{}", uuid);
    //
    //    return RUtil.success(true);
    //}


    ///**
    // * start to rent a charge ,it is only used to test tools
    // * @param map  the device uuid
    // * @return
    // */
    @ApiOperation(value = "start to rent a charge,it is only used to test tools")
    @ResponseBody
    @PostMapping("/sendMsg")
    public R sendMsg(@ApiJsonObject(name = "sendMsg_uuid ", value = {
            @ApiJsonProperty(key = "deviceUuid", example = "120706", description = "device uuid"),
            @ApiJsonProperty(key = "positionUuid", example = "120706", description = "position uuid")
    }) @RequestBody Map<String, Object> map) {
        String uuid = (String) map.get("deviceUuid");
        String positionUuid = String.valueOf(map.get("positionUuid"));
        log.info("sendMsg====param:deviceUuid:{}", uuid);
        return deviceService.sendMsg(uuid, positionUuid);
    }
    //
    ///**
    // * upload device q-code
    // *
    // * @param file
    // * @return
    // * @throws IOException
    // */
    //@ApiOperation(value = "upload device q-code")
    //@PostMapping("/uploadCode")
    //public R uploadCode(@RequestParam("file") MultipartFile file) throws IOException {
    //    //log.info("uploadCode====param:file:{}", uuid);
    //    String result = TestQRcode.decodeQR(file.getInputStream());
    //    return RUtil.success(result);
    //}


    ///**
    // * retrieve a warehouse information
    // * @param map  the postion uuid
    // * @param map  the machine uuid
    // * @param map  the device uuid
    // * @return
    // */
    //@ApiOperation(value = "retrieve a warehouse information")
    //@PostMapping("/getPowerbank")
    //public R getPowerbank(@ApiJsonObject(name = "getPowerbank_uuid_machineId_pos", value = {
    //        @ApiJsonProperty(key = "positionUuid", example = "4", description = "position id"),
    //        @ApiJsonProperty(key = "machineUuid", example = "1", description = "machine uuid"),
    //        @ApiJsonProperty(key = "deviceUuid", example = "120706", description = "device uuid")
    //}) @RequestBody Map<String, Object> map) {
    //
    //    String positionUuid = (String) map.get("positionUuid");
    //    String machineUuid = (String) map.get("machineUuid");
    //    String deviceUuid = (String) map.get("deviceUuid");
    //    log.info("getPowerbank====param:deviceUuid:{},machineUuid:{},positionUuid:{}", deviceUuid,machineUuid,positionUuid);
    //    return RUtil.success(deviceService.getPowerbank(positionUuid, machineUuid, deviceUuid));
    //
    //}

    ///**
    // * retrieve all warehouse information by device uuid
    // *
    // * @param map device_uuid
    // * @return
    // */
    //@ApiOperation(value = "retrieve all warehouse information by device uuid")
    //@PostMapping("/getPowerbanksByDeviceUuid")
    //public R getPowerbanksByDeviceUuid(
    //        @ApiJsonObject(name = "getPowerbanks_uuid", value = {
    //                @ApiJsonProperty(key = "deviceUuid", example = "120706", description = "device uuid")
    //        }) @RequestBody Map<String, Object> map) {
    //
    //    String uuid = (String) map.get("deviceUuid");
    //    log.info("getPowerbanksByDeviceUuid====param:uuid:{}", uuid);
    //    return RUtil.success(deviceService.getPowerbanks(uuid));
    //
    //}



    ///**
    // * get mqtt device's state , online or offline
    // * @param map  the device uuid
    // * @return
    // */
    //@ApiOperation(value = "get mqtt device's state , online or offline")
    //@ResponseBody
    //@PostMapping("/getDeviceState")
    //public R getDeviceState(@ApiJsonObject(name = "getDeviceState_uuid", value = {
    //        @ApiJsonProperty(key = "deviceUuid", example = "120706", description = "device uuid")
    //}) @RequestBody Map<String, Object> map) {
    //
    //    String uuid = (String) map.get("deviceUuid");
    //    log.info("getDeviceState====param:uuid:{}", uuid);
    //    return RUtil.success(deviceService.getDeviceState(uuid));
    //
    //}

    /**
     * scanner the q-code and start to rent
     * @param map the user id
     * @param map the device uuid
     */
    @ApiOperation(value = "the q-code and start to rent")
    @ResponseBody
    @PostMapping("/startRent")
    R startRent(@ApiJsonObject(name = "startRent_userId_uuid", value = {
            @ApiJsonProperty(key = "userId", example = "xx107r6", description = "wxaUserId"),
            @ApiJsonProperty(key = "deviceUuid", example = "120706", description = "device uuid"),
            @ApiJsonProperty(key = "positionUuid", example = "120706", description = "positionUuid"),
            @ApiJsonProperty(key = "orderNo", example = "120706", description = "orderNo")
    }) @RequestBody Map<String, Object> map){
        String userId = String.valueOf(map.get("userId"));
        String uuid = (String) map.get("deviceUuid");
        String positionUuid = (String) map.get("positionUuid");
        String orderNo = (String) map.get("orderNo");
        log.info("startRent====param:userId :{},==== uuid:{}", userId,uuid);
        return RUtil.success(deviceService.startRent(userId, uuid, positionUuid, orderNo));
    }



    /**
     * modify the device state
     * @param map the device uuid
     * @param map the device state
     * @param map the device ip address
     * @return
     */
    @ApiOperation(value = "modify the device state")
    @ResponseBody
    @PostMapping("/updateState")
    R updateState(@ApiJsonObject(name = "updateState_uuid_state_ip", value = {
            @ApiJsonProperty(key = "deviceUuid", example = "862506049385829", description = "device uuid"),
            @ApiJsonProperty(key = "deviceState", example = "1", description = "device state"),
            @ApiJsonProperty(key = "ip", example = "217.66.154.140", description = "device ip")
    }) @RequestBody Map<String, Object> map){
        String uuid = (String) map.get("deviceUuid");
        String state = (String) map.get("deviceState");
        String ip = (String) map.get("ip");
        log.info("updateState====param:uuid:{},state:{},ip;{}", uuid,state,ip);
        return RUtil.success(deviceService.updateState(uuid, Integer.parseInt(state),ip));
    }


    /**
     * set all netty device to offline,not mqtt device
     */
    void updateNettyDeviceToOffline(){
        log.info("updateNettyDeviceToOffline====");
        deviceService.updateNettyDeviceToOffline();
    }


    ///**
    // * retrieve the devices through device version information
    // * @param map the query Conditions
    // * @return
    // */
    //@ApiOperation(value = "retrieve the devices through device version information")
    //@ResponseBody
    //@PostMapping("/selectDeviceInfoByHardVersion")
    //R selectDeviceInfoByHardVersion(
    //        @ApiJsonObject(name = "selectDeviceInfo_hardwareVersion", value = {
    //                @ApiJsonProperty(key = "versionInfo", example = "v1.23", description = "version Infomation")
    //        }) @RequestBody Map<String, Object> map){
    //    String versionInfo = (String) map.get("versionInfo");
    //    log.info("selectDeviceInfoByHardVersion====param:versionInfo:{}", versionInfo);
    //    return RUtil.success(deviceService.selectDeviceInfoByHardVersion(versionInfo));
    //}




    ///**
    // * retrieve the devices through protocol version information
    // * @param map the query Conditions
    // * @return
    // */
    //@ApiOperation(value = "retrieve the devices through protocol version information")
    //@ResponseBody
    //@PostMapping("/selectDeviceInfoByProtocolVersion")
    //R selectDeviceInfoByProtocolVersion(
    //        @ApiJsonObject(name = "selectDeviceInfo_protocolVersionInfo", value = {
    //                @ApiJsonProperty(key = "versionInfo", example = "v2.53.01", description = "version Infomation")
    //        }) @RequestBody Map<String, Object> map){
    //    String versionInfo = (String) map.get("versionInfo");
    //    log.info("selectDeviceInfoByProtocolVersion====param:versionInfo:{}", versionInfo);
    //    return RUtil.success(deviceService.selectDeviceInfoByProtocolVersion(versionInfo));
    //}


    ///**
    // * add a  new devices information
    // * @param map the new device information
    // * @return
    // */
    //@ApiOperation(value = "add a  new devices information")
    //@ResponseBody
    //@PostMapping("/addDeviceInfo")
    //R addDeviceInfo(
    //        @ApiJsonObject(name = "addDeviceInfo_class_deviceInfo", value = {
    //                @ApiJsonProperty(key = "deviceNo", example = "xx-222-ddsf", description = "device number"),
    //                @ApiJsonProperty(key = "deviceName", example = "tdxxxx", description = "device name"),
    //                @ApiJsonProperty(key = "simNo", example = "32223443", description = "sim number "),
    //                @ApiJsonProperty(key = "spaceTotal", example = "36", description = "warehouse total"),
    //                @ApiJsonProperty(key = "machineTotal", example = "6", description = "mechine total"),
    //                @ApiJsonProperty(key = "deviceUuid", example = "1234024()", description = "device uuid"),
    //                @ApiJsonProperty(key = "hardwareVersion", example = "v3.20.3", description = "hardware version"),
    //                @ApiJsonProperty(key = "protocolVersion", example = "v2.1", description = "protocol version"),
    //                @ApiJsonProperty(key = "networkType", example = "（2G/3G/4G）", description = "network type"),
    //                @ApiJsonProperty(key = "url", example = "www.xxx.com:3322/callback", description = "callback url")
    //        }) @RequestBody Map<String, Object> map
    //){
    //    DeviceInfo deviceInfo = new DeviceInfo();
    //    if(map.get("deviceUuid")==null){
    //        return RUtil.error("the device uuid is required!");
    //    }
    //    if(map.get("networkType")==null){
    //       return RUtil.error("the network type is required!");
    //    }
    //    if(map.get("simNo")==null){
    //       return RUtil.error("the sim number is required!");
    //    }
    //    if(map.get("hardwareVersion")==null){
    //       return RUtil.error("the hardware version is required!");
    //    }
    //
    //    if(map.get("protocolVersion")==null){
    //       return RUtil.error("the protocol version is required!");
    //    }
    //
    //    parseMapToObject(deviceInfo,map);
    //    log.info("addDeviceInfo====param:deviceInfo:{}", deviceInfo.toString());
    //    return RUtil.success(deviceService.addDeviceInfo(deviceInfo));
    //}

    ///**
    // * delete a device by uuid
    // * @param map device uuid
    // * @return
    // */
    //@ApiOperation(value = "delete a device by uuid")
    //@ResponseBody
    //@PostMapping("/delDeviceInfo")
    //int delDeviceInfo(@ApiJsonObject(name = "delDeviceInfo_uuid", value = {
    //        @ApiJsonProperty(key = "deviceUuid", example = "120706", description = "device uuid")
    //}) @RequestBody Map<String, Object> map){
    //    String uuid = (String) map.get("deviceUuid");
    //    log.info("deviceUuid====param:deviceUuid:{}", uuid);
    //    return deviceService.delDeviceInfo(uuid);
    //}



    ///**
    // * edit a devices information
    // * @param map device information
    // * @return
    // */
    //@ApiOperation(value = "edit a device information")
    //@ResponseBody
    //@PostMapping("/editDeviceInfo")
    //R editDeviceInfo(
    //        @ApiJsonObject(name = "editDeviceInfo_class_deviceInfo", value = {
    //                @ApiJsonProperty(key = "deviceNo", example = "xx-222-ddsf", description = "device number"),
    //                @ApiJsonProperty(key = "deviceName", example = "tdxxxx", description = "device name"),
    //                @ApiJsonProperty(key = "simNo", example = "32223443", description = "sim number "),
    //                @ApiJsonProperty(key = "spaceTotal", example = "36", description = "wareHouse total"),
    //                @ApiJsonProperty(key = "machineTotal", example = "6", description = "mechine total"),
    //                @ApiJsonProperty(key = "deviceUuid", example = "1234024", description = "device uuid"),
    //                @ApiJsonProperty(key = "softwareVersion", example = "v1.03", description = "software version"),
    //                @ApiJsonProperty(key = "hardwareVersion", example = "v3.20.3", description = "hardware version"),
    //                @ApiJsonProperty(key = "protocolVersion", example = "v2.1", description = "protocol version"),
    //                @ApiJsonProperty(key = "networkType", example = "（2G、3G、4G）", description = "networkType"),
    //                @ApiJsonProperty(key = "url", example = "www.xxx.com:3322/callback", description = "callback host")
    //        }) @RequestBody Map<String, Object> map){
    //    if(map.get("deviceUuid")==null){
    //       return RUtil.error("the deviceUuid is required!");
    //    }
    //
    //    DeviceInfo deviceInfo = deviceService.getDeviceInfoByUuid(map.get("deviceUuid").toString());
    //    if(deviceInfo==null){
    //        return  RUtil.error("device isn't exsit");
    //    }
    //    parseMapToObject(deviceInfo,map);
    //    return RUtil.success(deviceService.editDeviceInfo(deviceInfo));
    //}



    ///**
    // * get warehouse information  by a rent order information
    // * @param map rent order number
    // * @return
    // */
    //@ApiOperation(value = "get warehouse information  by a rent order information")
    //@ResponseBody
    //@PostMapping("/getPowerbankDetailByOrderNo")
    //R getPowerbankDetailByOrderNo(
    //        @ApiJsonObject(name = "getPowerbankDetail_orderNo", value = {
    //                @ApiJsonProperty(key = "orderNo", example = "12422243545454", description = "order number"),
    //                @ApiJsonProperty(key = "deviceUuid", example = "120706", description = "device uuid"),
    //
    //        }) @RequestBody Map<String, Object> map){
    //    String orderNo = String.valueOf(map.get("orderNo"));
    //    String deviceUuid = String.valueOf(map.get("deviceUuid"));
    //    log.info("getPowerbankDetail====param:orderNo:{}", orderNo);
    //    return RUtil.success(deviceService.getPowerbankDetail(orderNo, deviceUuid));
    //}
    //
    //@ApiOperation(value = "get remaining power by driver uuid")
    //@ResponseBody
    //@PostMapping("/getRemainingPowerByUUid")
    //R getRemainingPowerByUUid(@ApiJsonObject(name = "getRemainingPowerByUUid", value = {
    //        @ApiJsonProperty(key = "deviceUuid", example = "865860042609660", description = "device uuid")
    //})@RequestBody() Map<String, Object> map) {
    //    String deviceUuid = (String) map.get("deviceUuid");
    //    return deviceService.getRemainingPower(deviceUuid);
    //}


//    @ApiOperation(value = "upgradeAll")
//    @ResponseBody
//    @PostMapping("/upgradeAll")
//    R upgradeAll(@ApiJsonObject(name = "upgradeAll ", value = {
//            @ApiJsonProperty(key = "type", example = "slave", description = "dtu:主机 slave:从机"),
//            @ApiJsonProperty(key = "filename", example = "WE20-6b6fee-MSV146.bin", description = "补丁文件名称")
//    }) @RequestBody Map<String, Object> map) {
////        主机升级，类型就是dtu，从机升级，类型就是slave
////        deviceService.
//        String type = String.valueOf(map.get("type"));
//        String filename = String.valueOf(map.get("filename"));
//        boolean upgrade = deviceService.upgradeAll(type, filename);
//        return RUtil.success(upgrade);
//    }

    //@ApiOperation(value = "uploadRadio")
    //@ResponseBody
    //@PostMapping("/uploadRadio")
    //R uploadRadio(@ApiJsonObject(name = "uploadRadio ", value = {
    //        @ApiJsonProperty(key = "deviceUuid", example = "865860042609660", description = "device uuid"),
    //        @ApiJsonProperty(key = "url", example = "", description = "广告下载链接,P地址/文件名/用户名/密码"),
    //        @ApiJsonProperty(key = "startDate", example = "2020/03/25", description = "有效日期开始,年/月/日"),
    //        @ApiJsonProperty(key = "endDate", example = "2020/03/26", description = "有效日期结束,年/月/日"),
    //        @ApiJsonProperty(key = "startTime", example = "08/00", description = "播放时间段开始,时/分"),
    //        @ApiJsonProperty(key = "endTime", example = "22/00", description = "播放时间段结束,时/分"),
    //        @ApiJsonProperty(key = "plays", example = "0", description = "播放次数,正整数,单位秒"),
    //        @ApiJsonProperty(key = "playTime", example = "0", description = "播放时长,正整数,单位秒")
    //}) @RequestBody Map<String, Object> map) {
    //    String deviceUuid = String.valueOf(map.get("deviceUuid"));
    //    String url = String.valueOf(map.get("url"));
    //    String startDate = String.valueOf(map.get("startDate"));
    //    String endDate = String.valueOf(map.get("endDate"));
    //    String startTime = String.valueOf(map.get("startTime"));
    //    String endTime = String.valueOf(map.get("endTime"));
    //    String plays = String.valueOf(map.get("plays"));
    //    String playTime = String.valueOf(map.get("playTime"));
    //    boolean result = deviceService.uploadRadio(deviceUuid, url, startDate, endDate, startTime, endTime, plays, playTime);
    //    return RUtil.success(result);
    //}

    //@ApiOperation(value = "uploadRadio")
    //@ResponseBody
    //@PostMapping("/deleteRadio")
    //R deleteRadio(@ApiJsonObject(name = "deleteRadio ", value = {
    //        @ApiJsonProperty(key = "deviceUuid", example = "865860042609660", description = "device uuid"),
    //        @ApiJsonProperty(key = "filename", example = "filename.mp4", description = "文件名")
    //}) @RequestBody Map<String, Object> map) {
    //    String deviceUuid = String.valueOf(map.get("deviceUuid"));
    //    String filename = String.valueOf(map.get("filename"));
    //    boolean result = deviceService.deleteRadio(deviceUuid, filename);
    //    return RUtil.success(result);
    //}
    //
    //@ApiOperation(value = "deleteAllResource")
    //@ResponseBody
    //@PostMapping("/deleteAllResource")
    //R deleteAllResource(@ApiJsonObject(name = "deleteAllResource ", value = {
    //        @ApiJsonProperty(key = "deviceUuid", example = "865860042609660", description = "device uuid")
    //}) @RequestBody Map<String, Object> map) {
    //    String deviceUuid = String.valueOf(map.get("deviceUuid"));
    //    boolean result = deviceService.deleteAllResource(deviceUuid);
    //    return RUtil.success(result);
    //}





    /**
     * Forced change the all warehouse's state
     * @param deviceUuid  the device uuid
     * @return
     */
    int alterAllPowerBackState(String deviceUuid){
        log.info("alterPowerState====param:deviceUuid:{}", deviceUuid);
        return deviceService.alterPowerState(deviceUuid);
    }



   private void parseMapToObject(DeviceInfo deviceInfo,Map map){
        deviceInfo.setDeviceNo(map.get("deviceNo")!=null?map.get("deviceNo").toString():"");
        deviceInfo.setDeviceName(map.get("deviceName")!=null?map.get("deviceName").toString():"");
        deviceInfo.setIccId(map.get("simNo")!=null?map.get("simNo").toString():"");
        deviceInfo.setSpaceNu(Integer.parseInt(map.get("spaceTotal")!=null?map.get("spaceTotal").toString():"-1"));
        deviceInfo.setMachineNu(Integer.parseInt(map.get("machineTotal")!=null?map.get("machineTotal").toString():"-1"));
        deviceInfo.setDeviceUuid(map.get("deviceUuid")!=null?map.get("deviceUuid").toString():"");
        deviceInfo.setSoftVersion(map.get("softwareVersion")!=null?map.get("softwareVersion").toString():"");
        deviceInfo.setHardVersion(map.get("hardwareVersion")!=null?map.get("hardwareVersion").toString():"");
        deviceInfo.setAgreementVersion(map.get("protocolVersion")!=null?map.get("protocolVersion").toString():"");
        deviceInfo.setNetworkType(map.get("networkType")!=null?map.get("networkType").toString():"");
        deviceInfo.setUrl(map.get("url")!=null?map.get("url").toString():"");
    }


}
