package com.td.common_service.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.td.common.model.base.BaseModel;

@Table(name = "order_pay")
public class OrderPay extends BaseModel implements Serializable {
    /**
     * 订单表id
     */
    @Id
    private Integer id;

    /**
     * 订单类型（0充值，1押金，2租借）
     */
    @Column(name = "order_type")
    private Integer orderType;

    /**
     * 状态0未支付，1已支付，2申请退款，3退款成功，4退款失败
     */
    @Column(name = "pay_state")
    private Integer payState;

    /**
     * 支付账号（用户openId）
     */
    @Column(name = "pay_count")
    private String payCount;

    /**
     * 付款钱数
     */
    @Column(name = "pay_money")
    private Integer payMoney;

    /**
     * 实际付款数
     */
    @Column(name = "real_money")
    private Integer realMoney;

    /**
     * 商户订单号
     */
    @Column(name = "out_trade_no")
    private String outTradeNo;

    /**
     * 微信订单号
     */
    @Column(name = "wx_out_trade_no")
    private String wxOutTradeNo;

    /**
     * 微信生成的预付订单号
     */
    @Column(name = "prepay_id")
    private String prepayId;

    /**
     * 有效时间，毫秒为单位
     */
    @Column(name = "expires_in")
    private Integer expiresIn;

    /**
     * 交易类型（JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付）小程序取值如下：JSAPI
     */
    @Column(name = "trade_type")
    private String tradeType;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private Date createdTime;

    /**
     * 支付完成时间
     */
    @Column(name = "pay_time")
    private Date payTime;

    /**
     * 申请退款时间
     */
    @Column(name = "apply_refund_time")
    private Date applyRefundTime;

    /**
     * 退款钱数
     */
    @Column(name = "refund_money")
    private Integer refundMoney;

    /**
     * 退款时间
     */
    @Column(name = "refund_time")
    private Date refundTime;

    /**
     * 支付类型，0微信，1支付宝
     */
    @Column(name = "pay_type")
    private Integer payType;

    private static final long serialVersionUID = 1L;

    /**
     * 获取订单表id
     *
     * @return id - 订单表id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置订单表id
     *
     * @param id 订单表id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取订单类型（0充值，1押金，2租借）
     *
     * @return order_type - 订单类型（0充值，1押金，2租借）
     */
    public Integer getOrderType() {
        return orderType;
    }

    /**
     * 设置订单类型（0充值，1押金，2租借）
     *
     * @param orderType 订单类型（0充值，1押金，2租借）
     */
    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    /**
     * 获取状态0未支付，1已支付，2申请退款，3退款成功，4退款失败
     *
     * @return pay_state - 状态0未支付，1已支付，2申请退款，3退款成功，4退款失败
     */
    public Integer getPayState() {
        return payState;
    }

    /**
     * 设置状态0未支付，1已支付，2申请退款，3退款成功，4退款失败
     *
     * @param payState 状态0未支付，1已支付，2申请退款，3退款成功，4退款失败
     */
    public void setPayState(Integer payState) {
        this.payState = payState;
    }

    /**
     * 获取支付账号（用户openId）
     *
     * @return pay_count - 支付账号（用户openId）
     */
    public String getPayCount() {
        return payCount;
    }

    /**
     * 设置支付账号（用户openId）
     *
     * @param payCount 支付账号（用户openId）
     */
    public void setPayCount(String payCount) {
        this.payCount = payCount == null ? null : payCount.trim();
    }

    /**
     * 获取付款钱数
     *
     * @return pay_money - 付款钱数
     */
    public Integer getPayMoney() {
        return payMoney;
    }

    /**
     * 设置付款钱数
     *
     * @param payMoney 付款钱数
     */
    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }

    /**
     * 获取实际付款数
     *
     * @return real_money - 实际付款数
     */
    public Integer getRealMoney() {
        return realMoney;
    }

    /**
     * 设置实际付款数
     *
     * @param realMoney 实际付款数
     */
    public void setRealMoney(Integer realMoney) {
        this.realMoney = realMoney;
    }

    /**
     * 获取商户订单号
     *
     * @return out_trade_no - 商户订单号
     */
    public String getOutTradeNo() {
        return outTradeNo;
    }

    /**
     * 设置商户订单号
     *
     * @param outTradeNo 商户订单号
     */
    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }

    /**
     * 获取微信订单号
     *
     * @return wx_out_trade_no - 微信订单号
     */
    public String getWxOutTradeNo() {
        return wxOutTradeNo;
    }

    /**
     * 设置微信订单号
     *
     * @param wxOutTradeNo 微信订单号
     */
    public void setWxOutTradeNo(String wxOutTradeNo) {
        this.wxOutTradeNo = wxOutTradeNo == null ? null : wxOutTradeNo.trim();
    }

    /**
     * 获取微信生成的预付订单号
     *
     * @return prepay_id - 微信生成的预付订单号
     */
    public String getPrepayId() {
        return prepayId;
    }

    /**
     * 设置微信生成的预付订单号
     *
     * @param prepayId 微信生成的预付订单号
     */
    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId == null ? null : prepayId.trim();
    }

    /**
     * 获取有效时间，毫秒为单位
     *
     * @return expires_in - 有效时间，毫秒为单位
     */
    public Integer getExpiresIn() {
        return expiresIn;
    }

    /**
     * 设置有效时间，毫秒为单位
     *
     * @param expiresIn 有效时间，毫秒为单位
     */
    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * 获取交易类型（JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付）小程序取值如下：JSAPI
     *
     * @return trade_type - 交易类型（JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付）小程序取值如下：JSAPI
     */
    public String getTradeType() {
        return tradeType;
    }

    /**
     * 设置交易类型（JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付）小程序取值如下：JSAPI
     *
     * @param tradeType 交易类型（JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付）小程序取值如下：JSAPI
     */
    public void setTradeType(String tradeType) {
        this.tradeType = tradeType == null ? null : tradeType.trim();
    }

    /**
     * 获取创建时间
     *
     * @return created_time - 创建时间
     */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * 设置创建时间
     *
     * @param createdTime 创建时间
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取支付完成时间
     *
     * @return pay_time - 支付完成时间
     */
    public Date getPayTime() {
        return payTime;
    }

    /**
     * 设置支付完成时间
     *
     * @param payTime 支付完成时间
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    /**
     * 获取申请退款时间
     *
     * @return apply_refund_time - 申请退款时间
     */
    public Date getApplyRefundTime() {
        return applyRefundTime;
    }

    /**
     * 设置申请退款时间
     *
     * @param applyRefundTime 申请退款时间
     */
    public void setApplyRefundTime(Date applyRefundTime) {
        this.applyRefundTime = applyRefundTime;
    }

    /**
     * 获取退款钱数
     *
     * @return refund_money - 退款钱数
     */
    public Integer getRefundMoney() {
        return refundMoney;
    }

    /**
     * 设置退款钱数
     *
     * @param refundMoney 退款钱数
     */
    public void setRefundMoney(Integer refundMoney) {
        this.refundMoney = refundMoney;
    }

    /**
     * 获取退款时间
     *
     * @return refund_time - 退款时间
     */
    public Date getRefundTime() {
        return refundTime;
    }

    /**
     * 设置退款时间
     *
     * @param refundTime 退款时间
     */
    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    /**
     * 获取支付类型，0微信，1支付宝
     *
     * @return pay_type - 支付类型，0微信，1支付宝
     */
    public Integer getPayType() {
        return payType;
    }

    /**
     * 设置支付类型，0微信，1支付宝
     *
     * @param payType 支付类型，0微信，1支付宝
     */
    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderType=").append(orderType);
        sb.append(", payState=").append(payState);
        sb.append(", payCount=").append(payCount);
        sb.append(", payMoney=").append(payMoney);
        sb.append(", realMoney=").append(realMoney);
        sb.append(", outTradeNo=").append(outTradeNo);
        sb.append(", wxOutTradeNo=").append(wxOutTradeNo);
        sb.append(", prepayId=").append(prepayId);
        sb.append(", expiresIn=").append(expiresIn);
        sb.append(", tradeType=").append(tradeType);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", payTime=").append(payTime);
        sb.append(", applyRefundTime=").append(applyRefundTime);
        sb.append(", refundMoney=").append(refundMoney);
        sb.append(", refundTime=").append(refundTime);
        sb.append(", payType=").append(payType);
        sb.append("]");
        return sb.toString();
    }
}