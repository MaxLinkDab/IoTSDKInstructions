package com.td.common_service.mapper;

import com.td.common.mapper.base.BaseMapper;
import com.td.common_service.model.MPlaceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface MPlaceInfoMapper extends BaseMapper<MPlaceInfo> {

    @Select("SELECT * FROM m_place_info ORDER BY id DESC")
    List<Map<String,Object>> findByLogAndLat();

    @Select("SELECT * FROM m_place_info where place_name like '%${name}%' ORDER BY id DESC LIMIT ${pageNumber},${size}")
    List<Map<String,Object>> findByName(@Param("name") String name, @Param("pageNumber") int pageNumber, @Param("size") int size);

    @Select("SELECT place_name FROM m_place_info where place_uuid={uuid} LIMIT 0,1")
    String getAddress(String uuid);
}