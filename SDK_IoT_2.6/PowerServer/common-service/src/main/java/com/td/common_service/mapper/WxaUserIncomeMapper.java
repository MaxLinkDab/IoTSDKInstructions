package com.td.common_service.mapper;

import com.td.common.mapper.base.BaseMapper;
import com.td.common_service.model.WxaUserIncome;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface WxaUserIncomeMapper extends BaseMapper<WxaUserIncome> {
	@Select("SELECT * FROM wxa_user_income  where user_id = ${userId} ORDER BY ${fullordering} desc limit ${pageNumber}, ${pageSize}")
	List<WxaUserIncome> getUserIncome(Map<String, Object> params);

	@Select("SELECT * FROM wxa_user_income  where user_id = ${userId} ORDER BY ${fullordering} desc limit ${pageNumber}, ${pageSize}")
	List<Map<String, Object>> getUserIncomeList(Map<String, Object> params);
}