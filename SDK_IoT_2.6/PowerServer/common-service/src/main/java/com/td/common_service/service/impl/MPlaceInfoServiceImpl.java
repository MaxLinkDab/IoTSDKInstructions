package com.td.common_service.service.impl;

import com.td.common_service.mapper.MPlaceInfoMapper;
import com.td.common_service.mapper.PowerbankMapper;
import com.td.common_service.model.Powerbank;
import com.td.common_service.service.MPlaceInfoService;
import com.td.util.LatLng;
import com.td.util.LatLngUtil;
import com.td.util.UtilTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MPlaceInfoServiceImpl implements MPlaceInfoService {

    @Autowired
    private MPlaceInfoMapper mPlaceInfoMapper;


    /**
     * 根据位置获取附件的充电宝
     * @param lat
     * @param lng
     * @param radius
     * @return
     */
    public List<Map<String,Object>> getPlaceByLngAndLat(double lat, double lng, int radius) {
        List<Map<String,Object>> mPlaceInfos = mPlaceInfoMapper.findByLogAndLat();
        int length = mPlaceInfos.size();
        if (length > 0) {
            LatLng start = new LatLng(lat, lng);
            for (int j = 0; j < length; ) {
                Map<String,Object> mPlaceInfo = mPlaceInfos.get(j);
                LatLng end = new LatLng(Double.valueOf(mPlaceInfo.get("lat").toString()), Double.valueOf(mPlaceInfo.get("lon").toString()));
                if (LatLngUtil.getDistance(start, end) > radius) {
                    //判断是否超过范围，若超过范围则直接remove
                    mPlaceInfos.remove(mPlaceInfos.get(j));
                    j--;
                    length--;
                }else {
                    mPlaceInfo.put("distance", LatLngUtil.getDistance(start, end));
                    mPlaceInfo.put("width",41);
                    mPlaceInfo.put("height",48);
                    mPlaceInfo.put("iconPath","/images/markers.png");
                    mPlaceInfo.put("latitude",mPlaceInfo.get("lat"));
                    mPlaceInfo.put("longitude",mPlaceInfo.get("lon"));
                    mPlaceInfo.put("picture_url",mPlaceInfo.get("picture_url"));
                    mPlaceInfo.put("open_time",mPlaceInfo.get("open_time"));
                }
                j = j + 1;
            }
        }
        return mPlaceInfos;
    }





}
