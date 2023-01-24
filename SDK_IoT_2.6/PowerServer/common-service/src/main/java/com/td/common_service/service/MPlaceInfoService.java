package com.td.common_service.service;

import java.util.List;
import java.util.Map;

public interface MPlaceInfoService {

    List<Map<String,Object>> getPlaceByLngAndLat(double lat, double lng, int radius);

}
