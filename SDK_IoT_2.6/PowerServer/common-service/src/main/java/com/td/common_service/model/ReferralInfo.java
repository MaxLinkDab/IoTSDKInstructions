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

@Setter
@Getter
@ToString
@Table(name = "referral_info")
public class ReferralInfo extends BaseModel implements Serializable {

    @Id
    private Integer id;

    @Column(name = "referral_user_id")
    private Integer referralUserId;

    @Column(name = "promoter_user_id")
    private Integer promoterUserId;

    @Column(name = "created_time")
    private Date createdTime;

}
