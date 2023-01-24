package com.td.common.vo;

import java.util.Date;

import com.td.common.vo.base.AbstractBaseVO;

import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Data
public class OrderRentPayVO{
    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 当前页显示数量
     */
    private Integer pageSize = 10;

    private Integer id;

    private String createTime;

    private String orderNo;

    private Integer userId;

    private String powerNo;

    private Integer money;

    private String memo;

    private Integer orderType;

    /**
     * (0已租借，1已归还,2已支付)
     */
    private Integer orderState;

    private Integer payWay;

    private String deviceUuid;

    private Date updateTime;

    private String closeTime;

    private String finishTime;

    private String orderPayNo;

    private String agentName;  //代理商名称
    private String soleNickname;//销售代表名称
    private String placeName;//场所名称
    private String placeNickname;//场所管理员名称
    private String chargeName;//套餐名称

    //    private String payCount; //用户openId
    private String openId; //用户openId

}