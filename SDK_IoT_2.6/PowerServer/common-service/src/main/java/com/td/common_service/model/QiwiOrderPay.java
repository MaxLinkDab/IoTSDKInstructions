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
@Table(name = "qiwi_order_pay")
public class QiwiOrderPay extends BaseModel implements Serializable {

    @Id
    private Integer id;

    @Column(name = "service_user_id")
    private int serviceUserId;

    @Column(name = "bill_id")
    private String billId;

    @Column(name = "payment_url")
    private String paymentUrl;

    /**
     * Set after close qiwi order
     */
    private Integer amount;

    /**
     * Create and wait 0, close 1, expired 2
     */
    private Integer state;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "close_time")
    private Date closeTime;

}
