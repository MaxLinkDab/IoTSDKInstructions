package com.td.task.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.td.common.model.base.BaseModel;

@Table(name = "t_device_log")
public class DeviceLog extends BaseModel implements Serializable {
    /**
     * PK
     */
    @Id
    @Column(name = "f_id")
    private Integer fId;

    /**
     * 设备编号
     */
    @Column(name = "f_device_no")
    private String fDeviceNo;

    /**
     * 类型（1:归还，2:上下线状态，3:生命周期变更）
     */
    @Column(name = "f_type")
    private Short fType;

    /**
     * 内容
     */
    @Column(name = "f_log")
    private String fLog;

    /**
     * 时间
     */
    @Column(name = "f_time")
    private Date fTime;

    /**
     * 创建时间
     */
    @Column(name = "f_create_time")
    private Date fCreateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 获取PK
     *
     * @return f_id - PK
     */
    public Integer getfId() {
        return fId;
    }

    /**
     * 设置PK
     *
     * @param fId PK
     */
    public void setfId(Integer fId) {
        this.fId = fId;
    }

    /**
     * 获取设备编号
     *
     * @return f_device_no - 设备编号
     */
    public String getfDeviceNo() {
        return fDeviceNo;
    }

    /**
     * 设置设备编号
     *
     * @param fDeviceNo 设备编号
     */
    public void setfDeviceNo(String fDeviceNo) {
        this.fDeviceNo = fDeviceNo == null ? null : fDeviceNo.trim();
    }

    /**
     * 获取类型（1:归还，2:上下线状态，3:生命周期变更）
     *
     * @return f_type - 类型（1:归还，2:上下线状态，3:生命周期变更）
     */
    public Short getfType() {
        return fType;
    }

    /**
     * 设置类型（1:归还，2:上下线状态，3:生命周期变更）
     *
     * @param fType 类型（1:归还，2:上下线状态，3:生命周期变更）
     */
    public void setfType(Short fType) {
        this.fType = fType;
    }

    /**
     * 获取内容
     *
     * @return f_log - 内容
     */
    public String getfLog() {
        return fLog;
    }

    /**
     * 设置内容
     *
     * @param fLog 内容
     */
    public void setfLog(String fLog) {
        this.fLog = fLog == null ? null : fLog.trim();
    }

    /**
     * 获取时间
     *
     * @return f_time - 时间
     */
    public Date getfTime() {
        return fTime;
    }

    /**
     * 设置时间
     *
     * @param fTime 时间
     */
    public void setfTime(Date fTime) {
        this.fTime = fTime;
    }

    /**
     * 获取创建时间
     *
     * @return f_create_time - 创建时间
     */
    public Date getfCreateTime() {
        return fCreateTime;
    }

    /**
     * 设置创建时间
     *
     * @param fCreateTime 创建时间
     */
    public void setfCreateTime(Date fCreateTime) {
        this.fCreateTime = fCreateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", fId=").append(fId);
        sb.append(", fDeviceNo=").append(fDeviceNo);
        sb.append(", fType=").append(fType);
        sb.append(", fLog=").append(fLog);
        sb.append(", fTime=").append(fTime);
        sb.append(", fCreateTime=").append(fCreateTime);
        sb.append("]");
        return sb.toString();
    }
}