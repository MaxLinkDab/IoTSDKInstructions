package com.td.common_service.model;

import com.td.common.model.base.BaseModel;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "wxa_user")
public class WxaUser extends BaseModel implements Serializable {
    /**
     * 小程序用户id
     */
    @Id
    private Integer id;

    /**
     * 昵称
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 头像
     */
    @Column(name = "head_url")
    private String headUrl;

    /**
     * 0未知，1男 2女
     */
    private Integer sex;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 押金
     */
    private Integer rent;

    /**
     * 用户类型，0没有交押金，1交了
     */
    @Column(name = "user_type")
    private Integer userType;

    /**
     * 租借状态 0没租 1租了
     */
    @Column(name = "loan_type")
    private Integer loanType;

    /**
     * 开放平台上绑定的唯一id
     */
    @Column(name = "union_id")
    private String unionId;

    /**
     * 对应公众号唯一id
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 用户uuid
     */
    private String uuid;

    /**
     * 0正常，1调试
     */
    private Integer debug;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private Date createdTime;

    /**
     * 国家
     */
    private String country;

    /**
     * 余额
     */
    private Integer money;

    private static final long serialVersionUID = 1L;

    /**
     * 获取小程序用户id
     *
     * @return id - 小程序用户id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置小程序用户id
     *
     * @param id 小程序用户id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取昵称
     *
     * @return nick_name - 昵称
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 设置昵称
     *
     * @param nickName 昵称
     */
    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    /**
     * 获取头像
     *
     * @return head_url - 头像
     */
    public String getHeadUrl() {
        return headUrl;
    }

    /**
     * 设置头像
     *
     * @param headUrl 头像
     */
    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl == null ? null : headUrl.trim();
    }

    /**
     * 获取0未知，1男 2女
     *
     * @return sex - 0未知，1男 2女
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置0未知，1男 2女
     *
     * @param sex 0未知，1男 2女
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取省份
     *
     * @return province - 省份
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省份
     *
     * @param province 省份
     */
    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    /**
     * 获取城市
     *
     * @return city - 城市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置城市
     *
     * @param city 城市
     */
    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    /**
     * 获取押金
     *
     * @return rent - 押金
     */
    public Integer getRent() {
        return rent;
    }

    /**
     * 设置押金
     *
     * @param rent 押金
     */
    public void setRent(Integer rent) {
        this.rent = rent;
    }

    /**
     * 获取用户类型，0没有交押金，1交了
     *
     * @return user_type - 用户类型，0没有交押金，1交了
     */
    public Integer getUserType() {
        return userType;
    }

    /**
     * 设置用户类型，0没有交押金，1交了
     *
     * @param userType 用户类型，0没有交押金，1交了
     */
    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    /**
     * 获取租借状态 0没租 1租了
     *
     * @return loan_type - 租借状态 0没租 1租了
     */
    public Integer getLoanType() {
        return loanType;
    }

    /**
     * 设置租借状态 0没租 1租了
     *
     * @param loanType 租借状态 0没租 1租了
     */
    public void setLoanType(Integer loanType) {
        this.loanType = loanType;
    }

    /**
     * 获取开放平台上绑定的唯一id
     *
     * @return union_id - 开放平台上绑定的唯一id
     */
    public String getUnionId() {
        return unionId;
    }

    /**
     * 设置开放平台上绑定的唯一id
     *
     * @param unionId 开放平台上绑定的唯一id
     */
    public void setUnionId(String unionId) {
        this.unionId = unionId == null ? null : unionId.trim();
    }

    /**
     * 获取对应全局唯一id
     *
     * @return open_id - 对应全局唯一id
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置对应公众唯一id
     *
     * @param openId 对应公众唯一id
     */
    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    /**
     * 获取用户uuid
     *
     * @return uuid - 用户uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 设置用户uuid
     *
     * @param uuid 用户uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    /**
     * 获取0正常，1调试
     *
     * @return debug - 0正常，1调试
     */
    public Integer getDebug() {
        return debug;
    }

    /**
     * 设置0正常，1调试
     *
     * @param debug 0正常，1调试
     */
    public void setDebug(Integer debug) {
        this.debug = debug;
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
     * 获取国家
     *
     * @return country - 国家
     */
    public String getCountry() {
        return country;
    }

    /**
     * 设置国家
     *
     * @param country 国家
     */
    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    /**
     * 获取余额
     *
     * @return money - 余额
     */
    public Integer getMoney() {
        return money;
    }

    /**
     * 设置余额
     *
     * @param money 余额
     */
    public void setMoney(Integer money) {
        this.money = money;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", nickName=").append(nickName);
        sb.append(", headUrl=").append(headUrl);
        sb.append(", sex=").append(sex);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", rent=").append(rent);
        sb.append(", userType=").append(userType);
        sb.append(", loanType=").append(loanType);
        sb.append(", unionId=").append(unionId);
        sb.append(", openId=").append(openId);
        sb.append(", uuid=").append(uuid);
        sb.append(", debug=").append(debug);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", country=").append(country);
        sb.append(", money=").append(money);
        sb.append("]");
        return sb.toString();
    }
}