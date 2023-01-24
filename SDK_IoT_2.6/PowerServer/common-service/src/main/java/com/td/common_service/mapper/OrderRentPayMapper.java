package com.td.common_service.mapper;

import com.td.common.mapper.base.BaseMapper;
import com.td.common_service.model.OrderRentPay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderRentPayMapper extends BaseMapper<OrderRentPay> {

    @Select("SELECT o.*,m.place_name FROM order_rent_pay o LEFT JOIN device_info di ON o.device_uuid = di.device_uuid LEFT JOIN m_place_info m ON m.place_uid = di.place_uid WHERE o.order_state > -1 AND o.user_id = ${userId} ORDER BY ${fullordering} desc limit ${pageNumber},${pageSize}")
    List<Map<String,Object>> getOrderRentByPaycount(Map<String,Object> map);

    @Select(" SELECT o.*,m.place_name FROM order_rent_pay o LEFT JOIN device_info di ON o.device_uuid = di.device_uuid LEFT JOIN m_place_info m ON m.place_uid = di.place_uid WHERE o.order_no = #{orderNo} and o.device_uuid=#{deviceUuid}")
    Map<String,Object> getOrderRentDetail(@Param("orderNo") String orderNo, @Param("deviceUuid") String deviceUuid);

    @Select("SELECT mci.default_charge FROM device_info di LEFT JOIN m_place_info mpi ON mpi.place_uid=di.place_uid LEFT JOIN m_charge_info mci ON mci.charge_no=mpi.charge_no WHERE di.device_uuid =  #{uuid} ")
    Map<String,Object> getDefaultCharge(@Param("uuid") String uuid);

    @Select("SELECT * FROM order_rent_pay WHERE  device_uuid= #{uuid}  AND order_state = 2 AND  DATEDIFF(finish_time,NOW())=-1")
    List<OrderRentPay> selectByDeviceUuid(@Param("uuid") String uuid);

    @Select("SELECT * FROM order_rent_pay WHERE power_no = #{powerNo} AND order_state = 0 ORDER BY id DESC LIMIT 0,1")
    OrderRentPay selectByPowerNo(@Param("powerNo") String powerNo);

    /**
     * 根据充电宝ID查找1min中内最后一个订单
     * @param powerNo 充电宝ID
     * @return 订单详情
     */
    @Select("SELECT * FROM order_rent_pay WHERE power_no = #{powerNo} AND TIMESTAMPDIFF(SECOND,create_time,NOW())<60 ORDER BY create_time DESC LIMIT 0,1")
    OrderRentPay getLastOrderByBatteryWithin1min(@Param("powerNo") String powerNo);

    @Select("SELECT * FROM order_rent_pay WHERE user_id = #{userId} AND TIMESTAMPDIFF(SECOND,create_time,NOW())<60 ORDER BY create_time DESC LIMIT 0,1")
    OrderRentPay getLastOrderByUserWithin1min(@Param("userId") String userId);
}
