package com.td.common_service.mapper;

import com.td.common.mapper.base.BaseMapper;
import com.td.common_service.model.PlaceInfo;
import com.td.common_service.model.ServiceUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PlaceInfoMapper  extends BaseMapper<PlaceInfo> {

    @Select("SELECT place_name FROM place_info where place_uuid=#{uuid} LIMIT 0,1")
    String getAddress(String uuid);

}
