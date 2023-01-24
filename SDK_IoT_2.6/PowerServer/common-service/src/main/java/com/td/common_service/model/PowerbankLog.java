package com.td.common_service.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.td.common.model.base.BaseModel;
import com.td.common_service.mapper.packet.PowerBankPackInfo;

@Table(name = "powerbank_log")
public class PowerbankLog extends BaseModel implements Serializable {
    /**
     * 充电宝日志id
     */
    @Id
    private Integer id;

    /**
     * 仓位ID
     */
    @Column(name = "pos_uuid")
    private String posUuid;

    /**
     * 归还结果 0成功 1错误 2霍尔传感器异常 3电磁阀机械故障 4保留
     */
    @Column(name = "back_result")
    private String backResult;

    /**
     * 电压值
     */
    @Column(name = "power_ad")
    private String powerAd;

    /**
     * 充电宝id
     */
    @Column(name = "power_uuid")
    private String powerUuid;

    /**
     * 温度值
     */
    private String temp;

    /**
     * 充电宝充电状态 01 代表充电宝正在充电，00 未充电
     */
    @Column(name = "powerbank_state")
    private String powerbankState;

    /**
     * 对应findback_log
     */
    private Integer bid;

    private static final long serialVersionUID = 1L;

    /**
     * 获取充电宝日志id
     *
     * @return id - 充电宝日志id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置充电宝日志id
     *
     * @param id 充电宝日志id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取仓位ID
     *
     * @return pos_uuid - 仓位ID
     */
    public String getPosUuid() {
        return posUuid;
    }

    /**
     * 设置仓位ID
     *
     * @param posUuid 仓位ID
     */
    public void setPosUuid(String posUuid) {
        this.posUuid = posUuid == null ? null : posUuid.trim();
    }

    /**
     * 获取归还结果 0成功 1错误 2霍尔传感器异常 3电磁阀机械故障 4保留
     *
     * @return back_result - 归还结果 0成功 1错误 2霍尔传感器异常 3电磁阀机械故障 4保留
     */
    public String getBackResult() {
        return backResult;
    }

    /**
     * 设置归还结果 0成功 1错误 2霍尔传感器异常 3电磁阀机械故障 4保留
     *
     * @param backResult 归还结果 0成功 1错误 2霍尔传感器异常 3电磁阀机械故障 4保留
     */
    public void setBackResult(String backResult) {
        this.backResult = backResult == null ? null : backResult.trim();
    }

    /**
     * 获取电压值
     *
     * @return power_ad - 电压值
     */
    public String getPowerAd() {
        return powerAd;
    }

    /**
     * 设置电压值
     *
     * @param powerAd 电压值
     */
    public void setPowerAd(String powerAd) {
        this.powerAd = powerAd == null ? null : powerAd.trim();
    }

    /**
     * 获取充电宝id
     *
     * @return power_uuid - 充电宝id
     */
    public String getPowerUuid() {
        return powerUuid;
    }

    /**
     * 设置充电宝id
     *
     * @param powerUuid 充电宝id
     */
    public void setPowerUuid(String powerUuid) {
        this.powerUuid = powerUuid == null ? null : powerUuid.trim();
    }

    /**
     * 获取温度值
     *
     * @return temp - 温度值
     */
    public String getTemp() {
        return temp;
    }

    /**
     * 设置温度值
     *
     * @param temp 温度值
     */
    public void setTemp(String temp) {
        this.temp = temp == null ? null : temp.trim();
    }

    /**
     * 获取充电宝充电状态 01 代表充电宝正在充电，00 未充电
     *
     * @return powerbank_state - 充电宝充电状态 01 代表充电宝正在充电，00 未充电
     */
    public String getPowerbankState() {
        return powerbankState;
    }

    /**
     * 设置充电宝充电状态 01 代表充电宝正在充电，00 未充电
     *
     * @param powerbankState 充电宝充电状态 01 代表充电宝正在充电，00 未充电
     */
    public void setPowerbankState(String powerbankState) {
        this.powerbankState = powerbankState == null ? null : powerbankState.trim();
    }

    /**
     * 获取对应findback_log
     *
     * @return bid - 对应findback_log
     */
    public Integer getBid() {
        return bid;
    }

    /**
     * 设置对应findback_log
     *
     * @param bid 对应findback_log
     */
    public void setBid(Integer bid) {
        this.bid = bid;
    }


    public void parsePowerbankPacket(PowerBankPackInfo powerBankPackInfo){
        this.setPosUuid(powerBankPackInfo.getPosId());
        this.setBackResult(powerBankPackInfo.getBackRes());
        this.setPowerbankState(powerBankPackInfo.getPosState());
        this.setPowerAd("" + powerBankPackInfo.getPowerADInt());
        this.setPowerUuid(powerBankPackInfo.getPowerNo());
        this.setTemp(powerBankPackInfo.getTemp());
        this.setBid(0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", posUuid=").append(posUuid);
        sb.append(", backResult=").append(backResult);
        sb.append(", powerAd=").append(powerAd);
        sb.append(", powerUuid=").append(powerUuid);
        sb.append(", temp=").append(temp);
        sb.append(", powerbankState=").append(powerbankState);
        sb.append(", bid=").append(bid);
        sb.append("]");
        return sb.toString();
    }
}