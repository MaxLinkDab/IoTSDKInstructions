package com.td.common_service.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.td.common.model.base.BaseModel;

@Table(name = "powerbank_position_log")
public class PowerbankPositionLog extends BaseModel implements Serializable {
    /**
     * 租借充电宝日志id
     */
    @Id
    private Integer id;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 仓位id
     */
    @Column(name = "position_uuid")
    private String positionUuid;

    /**
     * 充电宝no
     */
    @Column(name = "power_no")
    private String powerNo;

    /**
     * 状态(0可租借，1待归还，2异常)
     */
    private Integer state;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private Date modifyTime;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 设备id
     */
    @Column(name = "device_uuid")
    private String deviceUuid;

    /**
     * 归还时间
     */
    @Column(name = "back_time")
    private Date backTime;

    private static final long serialVersionUID = 1L;

    /**
     * 获取租借充电宝日志id
     *
     * @return id - 租借充电宝日志id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置租借充电宝日志id
     *
     * @param id 租借充电宝日志id
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取仓位id
     *
     * @return position_uuid - 仓位id
     */
    public String getPositionUuid() {
        return positionUuid;
    }

    /**
     * 设置仓位id
     *
     * @param positionUuid 仓位id
     */
    public void setPositionUuid(String positionUuid) {
        this.positionUuid = positionUuid == null ? null : positionUuid.trim();
    }

    /**
     * 获取充电宝no
     *
     * @return power_no - 充电宝no
     */
    public String getPowerNo() {
        return powerNo;
    }

    /**
     * 设置充电宝no
     *
     * @param powerNo 充电宝no
     */
    public void setPowerNo(String powerNo) {
        this.powerNo = powerNo == null ? null : powerNo.trim();
    }

    /**
     * 获取状态(0可租借，1待归还，2异常)
     *
     * @return state - 状态(0可租借，1待归还，2异常)
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置状态(0可租借，1待归还，2异常)
     *
     * @param state 状态(0可租借，1待归还，2异常)
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
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
     * 获取设备id
     *
     * @return device_uuid - 设备id
     */
    public String getDeviceUuid() {
        return deviceUuid;
    }

    /**
     * 设置设备id
     *
     * @param deviceUuid 设备id
     */
    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid == null ? null : deviceUuid.trim();
    }

    /**
     * 获取归还时间
     *
     * @return back_time - 归还时间
     */
    public Date getBackTime() {
        return backTime;
    }

    /**
     * 设置归还时间
     *
     * @param backTime 归还时间
     */
    public void setBackTime(Date backTime) {
        this.backTime = backTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", createTime=").append(createTime);
        sb.append(", positionUuid=").append(positionUuid);
        sb.append(", powerNo=").append(powerNo);
        sb.append(", state=").append(state);
        sb.append(", modifyTime=").append(modifyTime);
        sb.append(", userId=").append(userId);
        sb.append(", deviceUuid=").append(deviceUuid);
        sb.append(", backTime=").append(backTime);
        sb.append("]");
        return sb.toString();
    }
}