package com.td.api.mapper;


import com.td.api.domain.FindBackLog;

import java.util.List;

public interface MFindBackLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FindBackLog record);

    int insertSelective(FindBackLog record);

    FindBackLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FindBackLog record);

    int updateByPrimaryKey(FindBackLog record);

    List<FindBackLog> select(FindBackLog findBackLog);
}