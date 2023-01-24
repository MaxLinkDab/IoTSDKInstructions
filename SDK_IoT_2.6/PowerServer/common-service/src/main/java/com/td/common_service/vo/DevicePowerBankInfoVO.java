package com.td.common_service.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 *  设备_显示充电宝仓位信息
 */
@Data
public class DevicePowerBankInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    /**
     * 编号
     */
    @Column(name = "power_no")
    private String powerNo;

    /**
     * 租借状态(0未租借，1已经租借)
     */
    private Integer state;

    /**
     * 充电宝名称
     */
    @Column(name = "power_name")
    private String powerName;

    /**
     * 电压值
     */
    @Column(name = "power_ad")
    private Integer powerAd;

    /**
     * 仓位id
     */
    @Column(name = "position_uuid")
    private String positionUuid;

    /**
     * 从机id
     */
    @Column(name = "machine_uuid")
    private String machineUuid;

    /**
     * 设备UUID
     */
    @Column(name = "device_uuid")
    private String deviceUuid;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private Date createdTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 归还时间
     */
    @Column(name = "back_time")
    private Date backTime;

    /**
     * 场地名称
     */
    @Column(name = "place_name")
    private String placeName;
    /**
     * 代理商名称
     */
    @Column(name = "agent_name")
    private String agentName;
}
