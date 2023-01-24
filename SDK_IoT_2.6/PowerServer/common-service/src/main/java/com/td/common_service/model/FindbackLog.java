package com.td.common_service.model;

import com.td.common.model.base.BaseModel;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "findback_log")
public class FindbackLog extends BaseModel implements Serializable {
    /**
     * 充电宝归还日志
     */
    @Id
    private Integer id;

    /**
     * 设备uuid
     */
    @Column(name = "device_uuid")
    private String deviceUuid;

    /**
     * 从机id(设备上传来的)
     */
    @Column(name = "machine_uuid")
    private String machineUuid;

    /**
     * 新归还事件 01有 00无
     */
    private String event;

    private Integer bid;

    /**
     * iot消息
     */
    @Column(name = "iot_message")
    private String iotMessage;

    /**
     * 接收时间
     */
    @Column(name = "receive_time")
    private Date receiveTime;

    /**
     * 执行时间
     */
    @Column(name = "exec_time")
    private Date execTime;

    /**
     * 上传时间
     */
    @Column(name = "created_time")
    private Date createdTime;

    private static final long serialVersionUID = 1L;

    /**
     * 获取充电宝归还日志
     *
     * @return id - 充电宝归还日志
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置充电宝归还日志
     *
     * @param id 充电宝归还日志
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取从机id(设备上传来的)
     *
     * @return machine_uuid - 从机id(设备上传来的)
     */
    public String getMachineUuid() {
        return machineUuid;
    }

    /**
     * 设置从机id(设备上传来的)
     *
     * @param machineUuid 从机id(设备上传来的)
     */
    public void setMachineUuid(String machineUuid) {
        this.machineUuid = machineUuid == null ? null : machineUuid.trim();
    }

    /**
     * 获取新归还事件 01有 00无
     *
     * @return event - 新归还事件 01有 00无
     */
    public String getEvent() {
        return event;
    }

    /**
     * 设置新归还事件 01有 00无
     *
     * @param event 新归还事件 01有 00无
     */
    public void setEvent(String event) {
        this.event = event == null ? null : event.trim();
    }

    /**
     * @return bid
     */
    public Integer getBid() {
        return bid;
    }

    /**
     * @param bid
     */
    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public String getIotMessage() {
        return iotMessage;
    }

    public void setIotMessage(String iotMessage) {
        this.iotMessage = iotMessage;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getExecTime() {
        return execTime;
    }

    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }

    /**
     * 获取上传时间
     *
     * @return created_time - 上传时间
     */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * 设置上传时间
     *
     * @param createdTime 上传时间
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deviceUuid=").append(deviceUuid);
        sb.append(", machineUuid=").append(machineUuid);
        sb.append(", event=").append(event);
        sb.append(", bid=").append(bid);
        sb.append(", createdTime=").append(createdTime);
        sb.append("]");
        return sb.toString();
    }
}
