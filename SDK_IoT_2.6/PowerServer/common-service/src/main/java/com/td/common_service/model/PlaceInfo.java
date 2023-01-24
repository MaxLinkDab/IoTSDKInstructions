package com.td.common_service.model;

import com.td.common.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Table(name = "place_info")
public class PlaceInfo extends BaseModel implements Serializable {

    @Id
    private Integer id;

    @Column(name = "place_uuid")
    private String placeUuid;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "place_remark")
    private String placeRemark;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "state")
    private Boolean state;

    @Column(name = "type")
    private Integer type;
}
