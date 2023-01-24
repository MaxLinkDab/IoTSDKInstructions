package com.td.api.controller;

import com.td.api.config.ApiJsonObject;
import com.td.api.config.ApiJsonProperty;
import com.td.common.utils.RUtil;
import com.td.common.vo.R;
import com.td.common_service.service.MPlaceInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("device")
@Api(value = "DevicePostionController", description = "device position")
public class DevicePostionController {


    @Autowired
    private MPlaceInfoService mPlaceInfoService;
    /**
     * find all battery device  in some area
     * @param map
     * @return
     */
    @ApiOperation(value = "find all battery device  in some area")
    @PostMapping("/getBatteryDeviceInArea")
    public R getBatteryDeviceInArea(@ApiJsonObject(name = "getBatteryDeviceInArea", value = {
            @ApiJsonProperty(key = "lon", example = "113.883080", description = "longitude"),
            @ApiJsonProperty(key = "lat", example = "22.553290", description = "latitude")
    })@RequestBody Map<String,String> map){
        double lat = Double.parseDouble(map.get("lat"));
        double lon = Double.parseDouble(map.get("lon"));
        List<Map<String,Object>> infos = mPlaceInfoService.getPlaceByLngAndLat(lat,lon,100);
        return RUtil.success(infos);
    }

}
