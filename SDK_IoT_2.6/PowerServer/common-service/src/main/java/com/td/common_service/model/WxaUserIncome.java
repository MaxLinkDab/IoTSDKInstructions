package com.td.common_service.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.td.common.model.base.BaseModel;

@Table(name = "wxa_user_income")
public class WxaUserIncome extends BaseModel implements Serializable {
    /**
     * 用户交易记录
     */
    @Id
    private Integer id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 类别 0租借充电宝，1充值押金，2提现
     */
    private Integer type;

    /**
     * 钱数（分为单位）
     */
    private Integer money;

    /**
     * 0加1减
     */
    private Integer state;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private Date createdTime;

    /**
     * 说明
     */
    private String content;

    private static final long serialVersionUID = 1L;

    /**
     * 获取用户交易记录
     *
     * @return id - 用户交易记录
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置用户交易记录
     *
     * @param id 用户交易记录
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取类别 0租借充电宝，1充值押金，2提现
     *
     * @return type - 类别 0租借充电宝，1充值押金，2提现
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类别 0租借充电宝，1充值押金，2提现
     *
     * @param type 类别 0租借充电宝，1充值押金，2提现
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取钱数（分为单位）
     *
     * @return money - 钱数（分为单位）
     */
    public Integer getMoney() {
        return money;
    }

    /**
     * 设置钱数（分为单位）
     *
     * @param money 钱数（分为单位）
     */
    public void setMoney(Integer money) {
        this.money = money;
    }

    /**
     * 获取0加1减
     *
     * @return state - 0加1减
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置0加1减
     *
     * @param state 0加1减
     */
    public void setState(Integer state) {
        this.state = state;
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
     * 获取说明
     *
     * @return content - 说明
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置说明
     *
     * @param content 说明
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", type=").append(type);
        sb.append(", money=").append(money);
        sb.append(", state=").append(state);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", content=").append(content);
        sb.append("]");
        return sb.toString();
    }
}