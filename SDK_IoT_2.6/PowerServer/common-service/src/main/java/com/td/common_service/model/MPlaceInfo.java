package com.td.common_service.model;

import com.td.common.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "m_place_info")
public class MPlaceInfo extends BaseModel implements Serializable {
    @Id
    private Integer id;

    /**
     * 对应_销售代表uid
     */
    @Column(name = "sole_uid")
    private Integer soleUid;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 套餐id
     */
    @Column(name = "charge_no")
    private String chargeNo;

    /**
     * 场所负责人uid
     */
    @Column(name = "place_uid")
    private Integer placeUid;

    /**
     * 场所名称
     */
    @Column(name = "place_name")
    private String placeName;

    /**
     * 场所说明
     */
    @Column(name = "place_remark")
    private String placeRemark;

    /**
     * 场所编号
     */
    @Column(name = "place_no")
    private String placeNo;


    /**
     * 经度
     */
    private Double lon;

    /**
     * 纬度
     */
    private Double lat;

    /**
     * 场所图片url
     */
    @Column(name = "picture_url")
    private String pictureUrl;

    /**
     * 营业时间
     */
    @Column(name = "open_time")
    private String openTime;

    private Integer state;

    private Integer type;


    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取套餐id
     *
     * @return charge_no - 套餐id
     */
    public String getChargeNo() {
        return chargeNo;
    }

    /**
     * 设置套餐id
     *
     * @param chargeNo 套餐id
     */
    public void setChargeNo(String chargeNo) {
        this.chargeNo = chargeNo == null ? null : chargeNo.trim();
    }

    /**
     * 获取对应_m_user_info 场所负责人uid (唯一)
     *
     * @return place_uid - 对应_m_user_info 场所负责人uid (唯一)
     */
    public Integer getPlaceUid() {
        return placeUid;
    }

    /**
     * 设置对应_m_user_info 场所负责人uid (唯一)
     *
     * @param placeUid 对应_m_user_info 场所负责人uid (唯一)
     */
    public void setPlaceUid(Integer placeUid) {
        this.placeUid = placeUid;
    }

    /**
     * 获取场所名称
     *
     * @return place_name - 场所名称
     */
    public String getPlaceName() {
        return placeName;
    }

    /**
     * 设置场所名称
     *
     * @param placeName 场所名称
     */
    public void setPlaceName(String placeName) {
        this.placeName = placeName == null ? null : placeName.trim();
    }

    /**
     * 获取场所说明(详细地址)
     *
     * @return place_remark - 场所说明(详细地址)
     */
    public String getPlaceRemark() {
        return placeRemark;
    }

    /**
     * 设置场所说明(详细地址)
     *
     * @param placeRemark 场所说明(详细地址)
     */
    public void setPlaceRemark(String placeRemark) {
        this.placeRemark = placeRemark == null ? null : placeRemark.trim();
    }

    /**
     * 获取场所编号
     *
     * @return place_no - 场所编号
     */
    public String getPlaceNo() {
        return placeNo;
    }

    /**
     * 设置场所编号
     *
     * @param placeNo 场所编号
     */
    public void setPlaceNo(String placeNo) {
        this.placeNo = placeNo == null ? null : placeNo.trim();
    }

    /**
     * 获取经度
     *
     * @return lon - 经度
     */
    public Double getLon() {
        return lon;
    }

    /**
     * 设置经度
     *
     * @param lon 经度
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * 获取纬度
     *
     * @return lat - 纬度
     */
    public Double getLat() {
        return lat;
    }

    /**
     * 设置纬度
     *
     * @param lat 纬度
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * 获取场所图片url
     *
     * @return picture_url - 场所图片url
     */
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     * 设置场所图片url
     *
     * @param pictureUrl 场所图片url
     */
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl == null ? null : pictureUrl.trim();
    }

    /**
     * 获取营业时间
     *
     * @return open_time - 营业时间
     */
    public String getOpenTime() {
        return openTime;
    }

    /**
     * 设置营业时间
     *
     * @param openTime 营业时间
     */
    public void setOpenTime(String openTime) {
        this.openTime = openTime == null ? null : openTime.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", chargeNo=").append(chargeNo);
        sb.append(", placeUid=").append(placeUid);
        sb.append(", placeName=").append(placeName);
        sb.append(", placeRemark=").append(placeRemark);
        sb.append(", placeNo=").append(placeNo);
        sb.append(", lon=").append(lon);
        sb.append(", lat=").append(lat);
        sb.append(", pictureUrl=").append(pictureUrl);
        sb.append(", openTime=").append(openTime);
        sb.append("]");
        return sb.toString();
    }
}