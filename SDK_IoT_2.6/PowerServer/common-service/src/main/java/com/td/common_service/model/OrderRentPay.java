package com.td.common_service.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.td.common.model.base.BaseModel;

@Table(name = "order_rent_pay")
public class OrderRentPay extends BaseModel implements Serializable {
    /**
     * 租借订单表id
     */
    @Id
    private Integer id;

    /**
     * 订单编号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 充电宝编号
     */
    @Column(name = "power_no")
    private String powerNo;

    /**
     * 订单金额
     */
    private Integer money;

    /**
     * 订单备注
     */
    private String memo;

    /**
     * 订单类型(0)
     */
    @Column(name = "order_type")
    private Integer orderType;

    /**
     * 订单状态(0已租借，1已归还)
     */
    @Column(name = "order_state")
    private Integer orderState;

    /**
     * 支付方式(0默认余额，1租金，2微信支付)
     */
    @Column(name = "pay_way")
    private Integer payWay;

    /**
     * 设备uuid
     */
    @Column(name = "device_uuid")
    private String deviceUuid;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 订单结算时间（停止计费时间）
     */
    @Column(name = "close_time")
    private Date closeTime;

    /**
     * 支付完成时间
     */
    @Column(name = "finish_time")
    private Date finishTime;

    /**
     * 关联order_pay 的out_trade_no
     */
    @Column(name = "order_pay_no")
    private String orderPayNo;

    private static final long serialVersionUID = 1L;

    /**
     * 获取租借订单表id
     *
     * @return id - 租借订单表id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置租借订单表id
     *
     * @param id 租借订单表id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取订单编号
     *
     * @return order_no - 订单编号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单编号
     *
     * @param orderNo 订单编号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取充电宝编号
     *
     * @return power_no - 充电宝编号
     */
    public String getPowerNo() {
        return powerNo;
    }

    /**
     * 设置充电宝编号
     *
     * @param powerNo 充电宝编号
     */
    public void setPowerNo(String powerNo) {
        this.powerNo = powerNo == null ? null : powerNo.trim();
    }

    /**
     * 获取订单金额
     *
     * @return money - 订单金额
     */
    public Integer getMoney() {
        return money;
    }

    /**
     * 设置订单金额
     *
     * @param money 订单金额
     */
    public void setMoney(Integer money) {
        this.money = money;
    }

    /**
     * 获取订单备注
     *
     * @return memo - 订单备注
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 设置订单备注
     *
     * @param memo 订单备注
     */
    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    /**
     * 获取订单类型(0)
     *
     * @return order_type - 订单类型(0)
     */
    public Integer getOrderType() {
        return orderType;
    }

    /**
     * 设置订单类型(0)
     *
     * @param orderType 订单类型(0)
     */
    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    /**
     * 获取订单状态(0已租借，1已归还)
     *
     * @return order_state - 订单状态(0已租借，1已归还)
     */
    public Integer getOrderState() {
        return orderState;
    }

    /**
     * 设置订单状态(0已租借，1已归还)
     *
     * @param orderState 订单状态(0已租借，1已归还)
     */
    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    /**
     * 获取支付方式(0默认余额，1租金，2微信支付)
     *
     * @return pay_way - 支付方式(0默认余额，1租金，2微信支付)
     */
    public Integer getPayWay() {
        return payWay;
    }

    /**
     * 设置支付方式(0默认余额，1租金，2微信支付)
     *
     * @param payWay 支付方式(0默认余额，1租金，2微信支付)
     */
    public void setPayWay(Integer payWay) {
        this.payWay = payWay;
    }

    /**
     * 获取设备uuid
     *
     * @return device_uuid - 设备uuid
     */
    public String getDeviceUuid() {
        return deviceUuid;
    }

    /**
     * 设置设备uuid
     *
     * @param deviceUuid 设备uuid
     */
    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid == null ? null : deviceUuid.trim();
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取订单结算时间（停止计费时间）
     *
     * @return close_time - 订单结算时间（停止计费时间）
     */
    public Date getCloseTime() {
        return closeTime;
    }

    /**
     * 设置订单结算时间（停止计费时间）
     *
     * @param closeTime 订单结算时间（停止计费时间）
     */
    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    /**
     * 获取支付完成时间
     *
     * @return finish_time - 支付完成时间
     */
    public Date getFinishTime() {
        return finishTime;
    }

    /**
     * 设置支付完成时间
     *
     * @param finishTime 支付完成时间
     */
    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * 获取关联order_pay 的out_trade_no
     *
     * @return order_pay_no - 关联order_pay 的out_trade_no
     */
    public String getOrderPayNo() {
        return orderPayNo;
    }

    /**
     * 设置关联order_pay 的out_trade_no
     *
     * @param orderPayNo 关联order_pay 的out_trade_no
     */
    public void setOrderPayNo(String orderPayNo) {
        this.orderPayNo = orderPayNo == null ? null : orderPayNo.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", userId=").append(userId);
        sb.append(", powerNo=").append(powerNo);
        sb.append(", money=").append(money);
        sb.append(", memo=").append(memo);
        sb.append(", orderType=").append(orderType);
        sb.append(", orderState=").append(orderState);
        sb.append(", payWay=").append(payWay);
        sb.append(", deviceUuid=").append(deviceUuid);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", closeTime=").append(closeTime);
        sb.append(", finishTime=").append(finishTime);
        sb.append(", orderPayNo=").append(orderPayNo);
        sb.append("]");
        return sb.toString();
    }
}