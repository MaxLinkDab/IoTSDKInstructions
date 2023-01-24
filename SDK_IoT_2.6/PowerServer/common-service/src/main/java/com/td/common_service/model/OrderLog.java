package com.td.common_service.model;

import com.td.common.model.base.BaseModel;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "order_log")
public class OrderLog extends BaseModel implements Serializable {
    /**
     * 订单日志id
     */
    @Id
    private Integer id;

    /**
     * 操作类型（1租借下发，2租借响应, 3归还响应）
     */
    @Column(name = "op_type")
    private Integer opType;

    /**
     * 模块号
     */
    @Column(name = "device_uuid")
    private String deviceUuid;

    /**
     * 订单号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 内容
     */
    @Column(name = "content")
    private String content;

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
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIotMessage(String iotMessage) {
        this.iotMessage = iotMessage;
    }

    public String getIotMessage() {
        return iotMessage;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", opType=").append(opType);
        sb.append(", deviceUuid=").append(deviceUuid);
        sb.append(", orderNo=").append(orderNo);
        sb.append(", createTime=").append(createTime);
        sb.append(", content=").append(content);
        sb.append("]");
        return sb.toString();
    }
}
