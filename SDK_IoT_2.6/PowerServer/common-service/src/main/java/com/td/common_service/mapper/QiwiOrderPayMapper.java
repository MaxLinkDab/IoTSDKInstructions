package com.td.common_service.mapper;

import com.td.common.mapper.base.BaseMapper;
import com.td.common_service.model.OrderRentPay;
import com.td.common_service.model.QiwiOrderPay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QiwiOrderPayMapper extends BaseMapper<QiwiOrderPay> {

    @Select("SELECT * FROM qiwi_order_pay WHERE bill_id = #{billId}")
    QiwiOrderPay selectByBillId(@Param("billId") String billId);

    @Select("SELECT * FROM qiwi_order_pay WHERE state = #{state}")
    List<QiwiOrderPay> selectByState(@Param("state") Integer state);

}
