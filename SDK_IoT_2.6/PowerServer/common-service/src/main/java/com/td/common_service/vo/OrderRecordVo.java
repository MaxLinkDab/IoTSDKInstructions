package com.td.common_service.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderRecordVo {

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 当前页显示数量
     */
    private Integer pageSize = 10;

    private Integer id;
    /**
     * 设备uuid
     */
    private String deviceUuid;

    /**
     * 类型（0代理商，1销售代表，2场所）
     */
    private Integer type;

    /**
     * 代理商名字
     */
    private String userName;

    /**
     * 销售代表的名字
     */
    private String soleName;

    /**
     * 场所名字
     */
    private String placeName;

    /**
     * 用户表id
     */
    private Integer userId;

    /**
     * 分成比例
     */
    private Integer divided;

    /**
     * 钱数
     */
    private Integer money;

    /**
     * 订单数
     */
    private Integer orderSize;

    /**
     * 结算日期格式为2019-05-12
     */
    private String dateOf;

    /**
     * 结算月份格式为2019-05
     */
    private String monthOf;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 场所管理者名字
     */
    private String placeUserName;


    private String beginTime;
    private String endTime;

    private String deviceName;
}
