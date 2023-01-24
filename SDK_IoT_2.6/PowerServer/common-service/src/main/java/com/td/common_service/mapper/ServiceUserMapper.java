package com.td.common_service.mapper;

import com.td.common.mapper.base.BaseMapper;
import com.td.common_service.model.ServiceUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ServiceUserMapper extends BaseMapper<ServiceUser> {
    @Select("SELECT * FROM service_user WHERE lc_id=#{lcId} LIMIT 0,1")
    ServiceUser getByLcid(@Param("lcId") Integer lcId);
}
