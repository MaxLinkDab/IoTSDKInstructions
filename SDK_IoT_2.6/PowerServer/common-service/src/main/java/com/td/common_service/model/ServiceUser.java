package com.td.common_service.model;

import com.td.common.model.base.BaseModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Table(name = "service_user")
public class ServiceUser extends BaseModel implements Serializable {

    @Id
    private Integer id;

    @Column(name = "lc_id")
    private Integer lcId;

    @Column(name = "is_card_linked")
    private Boolean isCardLinked;

    private String token;

    private Integer money;

    @Column(name = "min_deposit")
    private Integer minDeposit;

    @Column(name = "messenger_level")
    private Integer messengerLevel;

    @Column(name = "website_level")
    private Integer websiteLevel;

    @Column(name = "application_level")
    private Integer applicationLevel;
}
