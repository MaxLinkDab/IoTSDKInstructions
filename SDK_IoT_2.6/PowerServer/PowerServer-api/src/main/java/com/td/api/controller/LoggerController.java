package com.td.api.controller;

import com.td.api.config.ApiJsonObject;
import com.td.api.config.ApiJsonProperty;
import com.td.api.domain.FindBackLog;
import com.td.api.mapper.MFindBackLogMapper;
import com.td.common.utils.RUtil;
import com.td.common.vo.R;
import com.td.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("device")
@Api(value = "LoggerController", description = "device working log relevant")
public class LoggerController {

    @Resource
    private MFindBackLogMapper mFindBackLogMapper;


    /**find working log by time span and device uuid
     * @param map uuid:
     * @return
     */
    @ApiOperation(value = "find working log by time span and device uuid")
    @ResponseBody
    @PostMapping("/selectDeviceLog")
    public R sendMsg(@ApiJsonObject(name = "sendMsg_map", value = {
            @ApiJsonProperty(key = "uuid", example = "123456", description = "设备UUID(必填)"),
            @ApiJsonProperty(key = "startTime", example = "2019-07-02", description = "设备UUID"),
            @ApiJsonProperty(key = "endTime", example = "2019-07-02", description = "设备UUID")
    }) @RequestBody Map<String, Object> map) {


        String uuid = (String) map.get("uuid");
        if (StringUtils.isNotEmpty(uuid)) {
            String startTime = (String) map.get("startTime");
            String endTime = (String) map.get("endTime");
            FindBackLog findBackLog = new FindBackLog();
            findBackLog.setDeviceUuid(uuid);
            findBackLog.setDeviceUuid(startTime);
            findBackLog.setDeviceUuid(endTime);
            return RUtil.success(mFindBackLogMapper.select(findBackLog));
        } else {
            return RUtil.error("uuid为空");
        }


    }
}
