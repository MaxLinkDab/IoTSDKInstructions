package com.td.common_service.model;

import com.td.common.model.base.BaseModel;
import com.td.common_service.mapper.packet.PowerBankPackInfo;
import lombok.Getter;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class Powerbank extends BaseModel implements Serializable {
    /**
     * 充电宝id
     */
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
     * 0 - only station charge,
     * 1 - everywhere charge
     */
//    @Column(name = "donate_level")
//    @Getter
//    private Integer donateLevel;

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
     * 0正常，1异常
     */
    @Column(name = "error_state")
    private Integer errorState;

    /**
     * 总机柜位置的行位置
     */
    @Column(name = "all_position_uuid_row")
    private Integer allPositionUuidRow;

    /**
     * 总机柜位置的列位置
     */
    @Column(name = "all_position_uuid_col")
    private Integer allPositionUuidCol;

    /**
     * 总机顺序位置（从上到下，从左到右）
     */
    @Column(name = "all_position_uuild")
    private Integer allPositionUuild;

    /**
     * 充电开关
     */
    @Column(name = "charging_switch")
    private Boolean chargingSwitch;

    private static final long serialVersionUID = 1L;

    /**
     * 获取充电宝id
     *
     * @return id - 充电宝id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置充电宝id
     *
     * @param id 充电宝id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取编号
     *
     * @return power_no - 编号
     */
    public String getPowerNo() {
        return powerNo;
    }

    /**
     * 设置编号
     *
     * @param powerNo 编号
     */
    public void setPowerNo(String powerNo) {
        this.powerNo = powerNo == null ? null : powerNo.trim();
    }

    /**
     * 获取租借状态(0未租借，1已经租借)
     *
     * @return state - 租借状态(0未租借，1已经租借)
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置租借状态(0未租借，1已经租借)
     *
     * @param state 租借状态(0未租借，1已经租借)
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取充电宝名称
     *
     * @return power_name - 充电宝名称
     */
    public String getPowerName() {
        return powerName;
    }

    /**
     * 设置充电宝名称
     *
     * @param powerName 充电宝名称
     */
    public void setPowerName(String powerName) {
        this.powerName = powerName == null ? null : powerName.trim();
    }

    /**
     * 获取电压值
     *
     * @return power_ad - 电压值
     */
    public Integer getPowerAd() {
        return powerAd;
    }

    /**
     * 设置电压值
     *
     * @param powerAd 电压值
     */
    public void setPowerAd(Integer powerAd) {
        this.powerAd = powerAd;
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
     * 获取从机id
     *
     * @return machine_uuid - 从机id
     */
    public String getMachineUuid() {
        return machineUuid;
    }

    /**
     * 设置从机id
     *
     * @param machineUuid 从机id
     */
    public void setMachineUuid(String machineUuid) {
        this.machineUuid = machineUuid == null ? null : machineUuid.trim();
    }

    /**
     * 获取设备UUID
     *
     * @return device_uuid - 设备UUID
     */
    public String getDeviceUuid() {
        return deviceUuid;
    }

    /**
     * 设置设备UUID
     *
     * @param deviceUuid 设备UUID
     */
    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid == null ? null : deviceUuid.trim();
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
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    /**
     * 获取0正常，1异常
     *
     * @return error_state - 0正常，1异常
     */
    public Integer getErrorState() {
        return errorState;
    }

    /**
     * 设置0正常，1异常
     *
     * @param errorState 0正常，1异常
     */
    public void setErrorState(Integer errorState) {
        this.errorState = errorState;
    }

    /**
     * 获取总机柜位置的行位置
     *
     * @return all_position_uuid_row - 总机柜位置的行位置
     */
    public Integer getAllPositionUuidRow() {
        return allPositionUuidRow;
    }

    /**
     * 设置总机柜位置的行位置
     *
     * @param allPositionUuidRow 总机柜位置的行位置
     */
    public void setAllPositionUuidRow(Integer allPositionUuidRow) {
        this.allPositionUuidRow = allPositionUuidRow;
    }

    /**
     * 获取总机柜位置的列位置
     *
     * @return all_position_uuid_col - 总机柜位置的列位置
     */
    public Integer getAllPositionUuidCol() {
        return allPositionUuidCol;
    }

    /**
     * 设置总机柜位置的列位置
     *
     * @param allPositionUuidCol 总机柜位置的列位置
     */
    public void setAllPositionUuidCol(Integer allPositionUuidCol) {
        this.allPositionUuidCol = allPositionUuidCol;
    }

    /**
     * 获取总机顺序位置（从上到下，从左到右）
     *
     * @return all_position_uuild - 总机顺序位置（从上到下，从左到右）
     */
    public Integer getAllPositionUuild() {
        return allPositionUuild;
    }

    /**
     * 设置总机顺序位置（从上到下，从左到右）
     *
     * @param allPositionUuild 总机顺序位置（从上到下，从左到右）
     */
    public void setAllPositionUuild(Integer allPositionUuild) {
        this.allPositionUuild = allPositionUuild;
    }

    public Boolean getChargingSwitch() {
        return chargingSwitch;
    }

    public void setChargingSwitch(Boolean chargingSwitch) {
        this.chargingSwitch = chargingSwitch;
    }

    public void parsePowerBankPacket(PowerBankPackInfo powerBankPackInfo, String deviceUuid, String machineUuid ){
        this.setCreatedTime(new Date(System.currentTimeMillis()));
        this.setPowerAd(powerBankPackInfo.getPowerADInt());
        this.setPowerNo(powerBankPackInfo.getPowerNo());
        this.setState(0);
        this.setPositionUuid(powerBankPackInfo.getPosId());
        this.setMachineUuid(machineUuid);
        this.setDeviceUuid(deviceUuid);
        this.setErrorState(0);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", powerNo=").append(powerNo);
        sb.append(", state=").append(state);
        sb.append(", powerName=").append(powerName);
        sb.append(", powerAd=").append(powerAd);
        sb.append(", positionUuid=").append(positionUuid);
        sb.append(", machineUuid=").append(machineUuid);
        sb.append(", deviceUuid=").append(deviceUuid);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", backTime=").append(backTime);
        sb.append(", errorState=").append(errorState);
        sb.append(", allPositionUuidRow=").append(allPositionUuidRow);
        sb.append(", allPositionUuidCol=").append(allPositionUuidCol);
        sb.append(", allPositionUuild=").append(allPositionUuild);
        sb.append(", chargingSwitch=").append(chargingSwitch);
        sb.append("]");
        return sb.toString();
    }
}
