package com.td.common_service.service.impl;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.td.common.utils.RUtil;
import com.td.common.vo.R;
import com.td.common_service.mapper.*;
import com.td.common_service.model.*;
import com.td.common_service.service.OrderPayService;
import com.td.common_service.service.jedis.RedisService;
import com.td.util.UtilTool;
import com.td.util.suport.SequenceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 微信订单
 */
@Slf4j
@Service
public class OrderPayServiceImpl implements OrderPayService {

    @Autowired
    private OrderPayMapper orderPayMapper;

    @Autowired
    private OrderRentPayMapper rentPayMapper;
    @Autowired
    private ServiceUserMapper userMapper;
    @Autowired
    private WxaUserMapper wxaUserMapper;
    @Autowired
    private WxaUserIncomeMapper incomeMapper;
    @Autowired
    private PowerbankMapper powerbankMapper;

    @Autowired
    private WxPayService wxService;

    @Autowired
    private RedisService redisService;


    private ExecutorService executorService;

    //@PostConstruct
    public void rentOrderHandler() {
        executorService = ThreadPoolServiceImpl.getInstance();
        executorService.execute(() -> {
            while (true) {
                try {
                    OrderRentPay orderParam = new OrderRentPay();
                    orderParam.setOrderState(0);
                    List<OrderRentPay> rentPays = rentPayMapper.select(orderParam);
                    for (OrderRentPay orderRentPay : rentPays) {
                        Powerbank powerbankParam = new Powerbank();
                        powerbankParam.setPowerNo(orderRentPay.getPowerNo());
                        List<Powerbank> powerbanks = powerbankMapper.select(powerbankParam);
                        for (Powerbank powerbank : powerbanks) {
                            if (powerbank != null && powerbank.getState() == 0 && System.currentTimeMillis() - orderRentPay.getCreateTime().getTime() > 1000 * 30) {
                                updateOrderRentPay(powerbankParam);
                            }
                        }
                        Thread.sleep(50);
                    }
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取押金订单
     *
     * @param openId
     * @return
     */
    @Override
    public OrderPay getOrderPayByPayCount(String openId) {
        OrderPay orderPay = new OrderPay();
        orderPay.setPayCount(openId);
        orderPay.setOrderType(1);
        orderPay.setPayState(1);
        orderPay = orderPayMapper.selectOne(orderPay);
        if (UtilTool.isNull(orderPay)) {
            orderPay = new OrderPay();
            orderPay.setPayCount(openId);
            orderPay.setPayState(2);
            orderPay.setOrderType(1);
            orderPay = orderPayMapper.selectOne(orderPay);
        }
        return orderPay;
    }

    /**
     * 获取未支付租借订单
     *
     * @param userId
     * @return
     */
    @Override
    public OrderRentPay getOrderRentPayByUserId(int userId) {
        OrderRentPay rentPay = new OrderRentPay();
        rentPay.setUserId(userId);
        rentPay.setOrderState(0);
        rentPay = rentPayMapper.selectOne(rentPay);
        if (UtilTool.isNull(rentPay)) {
            rentPay = new OrderRentPay();
            rentPay.setUserId(userId);
            rentPay.setOrderState(1);
            rentPay = rentPayMapper.selectOne(rentPay);
        }
        return rentPay;
    }

    /**
     * 生成租借订单
     *
     * @param wxaUserId
     * @param powerbank
     * @return
     */
    @Override
    public OrderRentPay createdOrderRentPay(String wxaUserId, Powerbank powerbank, int state, String orderNo) {
        OrderRentPay rentPay = new OrderRentPay();
        rentPay.setOrderState(state);
        rentPay.setUserId(Integer.parseInt(wxaUserId));
        rentPay.setPowerNo(powerbank.getPowerNo());
        rentPay.setDeviceUuid(powerbank.getDeviceUuid());
        rentPay.setMemo(powerbank.getId() + "");
        rentPay.setMoney(0);

        rentPay.setOrderType(1);

        rentPay.setOrderNo(orderNo);
        rentPay.setCreateTime(new Date(System.currentTimeMillis()));
        rentPay.setUpdateTime(new Date(System.currentTimeMillis()));
        rentPayMapper.insertSelective(rentPay);
//		WxaUser user = Optional.ofNullable(userMapper.selectByPrimaryKey(rentPay.getUserId())).orElse(new WxaUser());
//		user.setId(rentPay.getUserId());
//		user.setNickName("");
//		userMapper.insertSelective(user);
        return rentPay;
    }

    /**
     * 更新订单状态
     *
     * @param powerbank
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderRentPay updateOrderRentPay(Powerbank powerbank) {
//		OrderRentPay orderParam =  rentPayMapper.selectByPowerNo(powerbank.getPowerNo());
        OrderRentPay orderParam = new OrderRentPay();
        orderParam.setPowerNo(powerbank.getPowerNo());
        orderParam.setOrderState(0);
        List<OrderRentPay> rentPays = rentPayMapper.select(orderParam);
        OrderRentPay order = rentPays.size() > 0 ? rentPays.get(rentPays.size() - 1) : orderParam;
        log.info("=-=-=-=-=- order " + order.toString());
        if (UtilTool.isNull(order) || UtilTool.isNull(order.getId()) || order.getOrderState() != 0) {
            log.info("----------归还订单为空");
            return orderParam;
        } else {
            ServiceUser userParam = new ServiceUser();
            userParam.setId(order.getUserId());
            Optional<ServiceUser> user = Optional.ofNullable(userMapper.selectByPrimaryKey(userParam));
            log.info("user参数为：" + order.getUserId());
            log.info("订单号为：" + order.getOrderNo());
/*			WxaUser user = userMapper.selectByPrimaryKey(order.getUserId());
			user.setLoanType(0);
			userMapper.updateByPrimaryKey(user);*/
            order.setOrderState(1);
            order.setCloseTime(new Date(System.currentTimeMillis()));
            order.setUpdateTime(new Date(System.currentTimeMillis()));
            order.setMoney(deductMoney(order, user.map(ServiceUser::getMessengerLevel).orElse(-1) == 0));
            //order.setMoney(1);
            rentPayMapper.updateByPrimaryKeySelective(order);
            log.info("订单" + order.getOrderNo() + "状态已经更改，但是还未支付");
            // 从押金或者余额中扣款
            log.info(userMapper.selectAll().stream().map(Objects::toString).collect(Collectors.joining(", ")));
            log.info(rentPayMapper.selectAll().stream().map(Objects::toString).collect(Collectors.joining(", ")));
            user.ifPresent(serviceUser -> updateUserMoney(serviceUser, order));
        }
        return order;
    }

    /**
     * 计算规则
     */
    public int deductMoney(OrderRentPay rentPay, boolean freeDay) {
        //Map<String, Object> m = rentPayMapper.getDefaultCharge(rentPay.getDeviceUuid());
        int dfaultCharge = 60;
        //if (!UtilTool.isNull(m)) {
        //dfaultCharge = Integer.valueOf(m.get("default_charge").toString()) ;
        //}
//		int money = UtilTool.calcTime(rentPay.getCreateTime().getTime()) * dfaultCharge;
        int money = UtilTool.calcMoney(rentPay.getCreateTime().getTime()+(freeDay?1000*60*60*24:0), 100, 2000, dfaultCharge);
        log.info("====充电宝对应订单 orderInfo{}: 费用：{}", new Object[]{rentPay.getOrderNo(), money});
        return money;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserMoney(ServiceUser user, OrderRentPay rentPay) {
        if (user.getMoney() - rentPay.getMoney() > 0) {
            user.setMoney(user.getMoney() - rentPay.getMoney());
            //userMapper.updateByPrimaryKey(user);
            rentPay.setOrderState(2);
            rentPay.setFinishTime(new Date(System.currentTimeMillis()));
            rentPay.setPayWay(0);
        } else {
            user.setMoney(0);
            if(user.getMessengerLevel()>0)user.setMinDeposit(rentPay.getMoney());
            //userMapper.updateByPrimaryKey(user);
            rentPay.setOrderState(2);
            rentPay.setFinishTime(new Date(System.currentTimeMillis()));
            rentPay.setPayWay(1);
        }
        userMapper.updateByPrimaryKey(user);
        rentPayMapper.updateByPrimaryKeySelective(rentPay);
        log.info("====订单号：{}已经用余额支付了{}钱 ", new Object[]{rentPay.getOrderNo(), rentPay.getMoney()});
    }

    /**
     * 更新用户余额或者押金，及其订单更改
     *
     * @param wxaUser
     * @param rentPay
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateUserMoney(WxaUser wxaUser, OrderRentPay rentPay) {
        if (wxaUser.getMoney() >= rentPay.getMoney()) {
            wxaUser.setMoney(wxaUser.getMoney() - rentPay.getMoney());
            wxaUserMapper.updateByPrimaryKey(wxaUser);
            rentPay.setOrderState(2);
            rentPay.setFinishTime(new Date(System.currentTimeMillis()));
            rentPay.setPayWay(0);
            rentPayMapper.updateByPrimaryKeySelective(rentPay);
            log.info("====订单号：{}已经用余额支付了{}钱 ", new Object[]{rentPay.getOrderNo(), rentPay.getMoney()});
            addWxaUserIncome(wxaUser, rentPay.getMoney(), 1, 0, "租借充电宝");
        } else {
            if (wxaUser.getRent() >= rentPay.getMoney()) {
                wxaUser.setRent(wxaUser.getRent() - rentPay.getMoney());
                wxaUserMapper.updateByPrimaryKey(wxaUser);
                rentPay.setOrderState(2);
                rentPay.setFinishTime(new Date(System.currentTimeMillis()));
                rentPay.setPayWay(1);
                rentPayMapper.updateByPrimaryKeySelective(rentPay);
                log.info("====订单号：{}已经用押金支付了{}钱 ", new Object[]{rentPay.getOrderNo(), rentPay.getMoney()});
                addWxaUserIncome(wxaUser, rentPay.getMoney(), 1, 0, "租借充电宝");
            } else {
                log.info("====订单号：{}不够支付{}钱 ", new Object[]{rentPay.getOrderNo(), rentPay.getMoney()});
            }
        }
        return 0;
    }

    /**
     * 用户钱数记录参数值
     *
     * @param wxaUser
     * @return
     */
    public int addWxaUserIncome(WxaUser wxaUser, int money, int state, int type, String content) {
        WxaUserIncome income = new WxaUserIncome();
        income.setMoney(money);
        income.setState(state);
        income.setType(type);
        income.setUserId(wxaUser.getId());
        income.setContent(content);
        income.setCreatedTime(new Date(System.currentTimeMillis()));
        incomeMapper.insertSelective(income);
        return 0;
    }

    /**
     * 生成订单
     */
    /**
     * 生成支付订单
     *
     * @param user
     * @param money
     * @return
     */
    @Override
    public OrderPay getOrderPay(WxaUser user, int money, int type) {
        OrderPay orderPay = new OrderPay();
        orderPay.setPayState(0);
        orderPay.setOrderType(type);
        orderPay.setPayCount(user.getOpenId());
        orderPay.setOutTradeNo(SequenceManager.createOrderNo() + "");
        orderPay.setPayMoney(money);
        orderPay.setTradeType("JSAPI");
        orderPay.setCreatedTime(new Date(System.currentTimeMillis()));
        orderPay.setPayType(0);
        orderPayMapper.insert(orderPay);
        return orderPay;
    }

    /**
     * 订单支付
     *
     * @param pay
     * @param userIp
     * @param user
     * @param type   0 押金支付，1充值支付
     * @return
     */
    @Override
    public R orderPay(OrderPay pay, String userIp, WxaUser user, int type) {
        String body = "充电宝押金-订单支付";
        if (type == 1) {
            body = "充电宝充值-订单支付";
        }
        log.info("开始获取参数========");
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        log.info("参数有无=======");
        orderRequest.setBody(body);
        orderRequest.setOutTradeNo(pay.getOutTradeNo());
        if (1 == user.getDebug()) {
            orderRequest.setTotalFee(1);
        } else {
            orderRequest.setTotalFee(pay.getPayMoney());
        }
        orderRequest.setOpenid(pay.getPayCount());
        orderRequest.setSpbillCreateIp(userIp);
        orderRequest.setTradeType("JSAPI");
        Object object;
        log.info("开始获取参数========");
        try {
            object = wxService.createOrder(orderRequest);
            log.info(user.getNickName() + "的支付参数：" + object);
        } catch (WxPayException e) {
            e.printStackTrace();
            return RUtil.error("支付失败");
        }
        return RUtil.success(object);
    }

    /**
     * 支付回调更新支付信息
     *
     * @param result
     * @return 0代表没有订单，1有
     */
    @Override
    public int updateOrderPay(WxPayOrderNotifyResult result) {
        log.info("回调参数：" + result);
        OrderPay orderPay = new OrderPay();
        orderPay.setOutTradeNo(result.getOutTradeNo());
        orderPay.setPayState(0);
        orderPay = orderPayMapper.selectOne(orderPay);
        if (UtilTool.isNull(orderPay)) {
            return 0;
        } else {
            orderPay.setPayState(1);
            orderPay.setRealMoney(result.getTotalFee());
            orderPay.setWxOutTradeNo(result.getTransactionId());
            orderPay.setPayTime(new Date(System.currentTimeMillis()));
            orderPay.setPayType(0);
            orderPayMapper.updateByPrimaryKey(orderPay);
            // 更新用户资产及其状态
            log.info("更新用户资产及其状态");
            int state = updateWxaUser(orderPay, 0);
            log.info("更新用户资产成功，状态为：" + state);
            return 1;
        }
    }

    /**
     * 退款回调更新支付信息
     *
     * @param result
     * @return
     */
    @Override
    public int updateRefundOrderPay(WxPayRefundNotifyResult result) {
        log.info("款回调更新支付信息：" + result);
        OrderPay orderPay = new OrderPay();
        orderPay.setOutTradeNo(result.getReqInfo().getOutTradeNo());
        orderPay.setPayState(2);
        orderPay = orderPayMapper.selectOne(orderPay);
        if (!UtilTool.isNull(orderPay)) {
            orderPay.setPayState(3);
            orderPay.setRefundMoney(result.getReqInfo().getRefundFee());
            orderPay.setRefundTime(new Date(System.currentTimeMillis()));
            orderPayMapper.updateByPrimaryKey(orderPay);
            updateWxaUser(orderPay, 1);
        }
        return 0;
    }

    /**
     * 用户申请退款
     *
     * @param wxaUser
     * @return
     */
    @Override
    public R refundPay(WxaUser wxaUser) {
        OrderPay orderPay = getOrderPayByPayCount(wxaUser.getOpenId());
        if (UtilTool.isNull(orderPay)) {
            return RUtil.error("您还未交押金，不能申请提现");
        } else if (orderPay.getPayState() == 2) {
            return RUtil.error("您已经申请提现了，稍后会到账的");
        } else {
            if (wxaUser.getRent() < 0) {
                return RUtil.error("您当前押金为0，不能提现");
            }
            WxPayRefundResult result = null;
            WxPayRefundRequest request = new WxPayRefundRequest();
            request.setOutRefundNo(UtilTool.getOrderCode());
            request.setTotalFee(orderPay.getRealMoney());
            int money = wxaUser.getRent();
            if (wxaUser.getRent() > orderPay.getRealMoney()) {
                money = orderPay.getRealMoney();
            }
            request.setRefundFee(money);
            request.setOutTradeNo(orderPay.getOutTradeNo());
            request.setNotifyUrl("http://wxc.tdotcn.com/pay/refundNotify");

            try {
                result = wxService.refund(request);
            } catch (WxPayException e) {
                e.printStackTrace();
            }
            orderPay.setPayState(2);
            orderPay.setApplyRefundTime(new Date(System.currentTimeMillis()));
            orderPayMapper.updateByPrimaryKey(orderPay);
            return RUtil.success();
        }
    }

    /**
     * 获取租借订单
     *
     * @param params
     * @return
     */
    @Override
    public List<Map<String, Object>> getOrderRentByCount(Map<String, Object> params) {
        List<Map<String, Object>> list = rentPayMapper.getOrderRentByPaycount(params);
        for (Map<String, Object> map : list) {
            OrderRentPay rentPay = new OrderRentPay();
            rentPay.setOrderNo(map.get("order_no").toString());
            rentPay = rentPayMapper.selectOne(rentPay);
            if (!UtilTool.isNull(map.get("close_time"))) {
                map.put("close_time", UtilTool.timeStamp2Date((Timestamp) map.get("close_time")));
            }
            map.put("create_time", UtilTool.timeStamp2Date((Timestamp) map.get("create_time")));
            int state = (int) map.get("order_state");
            if (state == 0) {
                map.put("money", getDynamicOrderRent(rentPay).get("money"));
            }
        }
        return list;
    }

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    @Override
    public Map<String, Object> getOrderRentDetail(String orderNo, String deviceUuid) {
/*		OrderRentPay rentPay = new OrderRentPay();
		rentPay.setOrderNo(orderNo);
		rentPay.setDeviceUuid(deviceUuid);
		rentPay = rentPayMapper.selectOne(rentPay);*/
        Map<String, Object> map = rentPayMapper.getOrderRentDetail(orderNo, deviceUuid);
        if (!UtilTool.isNull(map)) {
            int chargingTime = 0;
            if (Integer.valueOf(map.get("order_state").toString()) == 0) {
                chargingTime = UtilTool.calcTime((Timestamp) map.get("create_time"),
                        new Timestamp(System.currentTimeMillis()));
            } else if (Integer.valueOf(map.get("order_state").toString()) == 2) {
                chargingTime = UtilTool.calcTime((Timestamp) map.get("create_time"), (Timestamp) map.get("close_time"));
            }
            map.put("charging_time", chargingTime);
            if (!UtilTool.isNull(map.get("close_time"))) {
                map.put("close_time", UtilTool.timeStamp2Date((Timestamp) map.get("close_time")));
            }
            map.put("create_time", UtilTool.timeStamp2Date((Timestamp) map.get("create_time")));
            int state = (int) map.get("order_state");
            if (state == 0) {
//				map.put("money", getDynamicOrderRent(rentPay).get("money"));
            }
        }
        return map;
    }

    /**
     * 获取动态充电宝订单信息
     *
     * @param rentPay
     * @return
     */
    @Override
    public Map<String, Object> getDynamicOrderRent(OrderRentPay rentPay) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTool.isNull(rentPay)) {
            map.put("state", 0);
        } else {
            map.put("state", 1);
            map.put("orderNo", rentPay.getOrderNo());
            map.put("orderState", rentPay.getOrderState());
            if (rentPay.getOrderState() == 1) {
                map.put("money", rentPay.getMoney());
            } else {
                Map<String, Object> m = rentPayMapper.getDefaultCharge(rentPay.getDeviceUuid());
                int dfaultCharge = 200;
                if (!UtilTool.isNull(m)) {
                    dfaultCharge = Integer.valueOf(m.get("default_charge").toString());
                }
//				int money = UtilTool.calcTime(rentPay.getCreateTime().getTime()) * dfaultCharge;
                int money = 0;//UtilTool.calcMoney(rentPay.getCreateTime().getTime(), 2000, 9900, dfaultCharge);было
                if (money > 9900) {
                    money = 9900;
                }
                map.put("money", money);
            }
        }
        return map;
    }

    /**
     * 更新用户订单数据
     *
     * @param orderPay
     */
    @Override
    public void updateOrder(OrderPay orderPay) {
        if (orderPay.getPayState() == 1) {
            orderPay.setPayState(3);
            orderPayMapper.updateByPrimaryKeySelective(orderPay);
        }
    }


    /**
     * 更新用户资产及其状态
     *
     * @param state 0支付回调 1退款回调
     */
    public int updateWxaUser(OrderPay orderPay, int state) {
        WxaUser wxaUser = new WxaUser();
        wxaUser.setOpenId(orderPay.getPayCount());
        wxaUser = wxaUserMapper.selectOne(wxaUser);
        if (state == 0) {
            if (orderPay.getOrderType() == 1) {
                log.info("更改用户状态及其租金状态");
                wxaUser.setUserType(1);
                wxaUser.setRent(wxaUser.getRent() + orderPay.getPayMoney());
                addWxaUserIncome(wxaUser, orderPay.getPayMoney(), 0, 1, "充值押金");
            } else if (orderPay.getOrderType() == 0) {
                log.info("更改用户状态及其租金状态");
                wxaUser.setMoney(wxaUser.getMoney() + orderPay.getPayMoney());
                addWxaUserIncome(wxaUser, orderPay.getPayMoney(), 0, 0, "余额充值");
            }
        } else if (state == 1) {
            log.info("更改用户身份及其租金状态");
            wxaUser.setUserType(0);
            wxaUser.setRent(wxaUser.getRent() - orderPay.getRefundMoney());
            addWxaUserIncome(wxaUser, orderPay.getRefundMoney(), 1, 2, "提现");
        }
        wxaUserMapper.updateByPrimaryKey(wxaUser);
        return 0;
    }

    /**
     * 获取并且激活预订单
     *
     * @param userId
     * @param powerbank
     * @return
     */
    @Override
    public OrderRentPay getOrderRentPayByState(int userId, Powerbank powerbank) {
		/*OrderRentPay orderRentPay = new OrderRentPay();
		orderRentPay.setOrderState(-1);
		orderRentPay.setUserId(userId);
		orderRentPay.setPowerNo(powerbank.getPowerNo());*/
        OrderRentPay order = rentPayMapper.getLastOrderByBatteryWithin1min(powerbank.getPowerNo());
        if (order != null) {
            if (order.getUserId() == userId && order.getOrderState() == -1) {
                if (powerbank.getErrorState() != 1) {
                    order.setCreateTime(new Date(System.currentTimeMillis()));
                    order.setOrderState(0);
                    rentPayMapper.updateByPrimaryKeySelective(order);
                }
            } else {
                return null;
            }
        }
        return order;
    }

}
