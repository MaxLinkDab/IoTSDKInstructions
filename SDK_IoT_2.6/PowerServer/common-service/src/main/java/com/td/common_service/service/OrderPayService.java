package com.td.common_service.service;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.td.common.vo.R;
import com.td.common_service.model.OrderPay;
import com.td.common_service.model.OrderRentPay;
import com.td.common_service.model.Powerbank;
import com.td.common_service.model.WxaUser;

import java.util.List;
import java.util.Map;

public interface OrderPayService {


    OrderPay getOrderPayByPayCount(String openId);

    OrderRentPay getOrderRentPayByUserId(int userId);

    OrderRentPay createdOrderRentPay(String wxaUserId, Powerbank powerbank, int state, String orderNo);

    OrderRentPay updateOrderRentPay(Powerbank powerbank);

    int deductMoney(OrderRentPay rentPay,boolean freeDay);

    OrderPay getOrderPay(WxaUser user, int money, int type);

    R orderPay(OrderPay pay, String userIp, WxaUser user, int type);

    int updateOrderPay(WxPayOrderNotifyResult result);

    int updateRefundOrderPay(WxPayRefundNotifyResult result);

    R refundPay(WxaUser wxaUser);

    List<Map<String, Object>> getOrderRentByCount(Map<String, Object> params);

    Map<String, Object> getOrderRentDetail(String orderNo, String deviceUuid);

    Map<String, Object> getDynamicOrderRent(OrderRentPay rentPay);

    void updateOrder(OrderPay orderPay);


    OrderRentPay getOrderRentPayByState(int userId, Powerbank powerbank);
}
