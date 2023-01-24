package com.td.common_service.model;

import com.td.common.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@Setter
@Getter
@Table(name = "powerbank_info")
public class PowerbankInfo extends BaseModel implements Serializable {

    /**
     * Personal number of the powerbank
     */
    @Column(name = "power_no")
    private String powerNo;

    /**
     * 0 default - can select only recharge
     * 1 vip - can select recharge, clean
     * 2 premium - can select recharge, clean and ad
     */
    @Column(name = "donate_level")
    private Integer donateLevel;

    /**
     * Powerbank have recharge function
     */
    private Boolean recharge;

    /**
     * Powerbank was washed before use
     */
    private Boolean clean;

    /**
     * Can select only with subscribe
     *
     * 0% - new
     * 50% - old
     * 100% - trash
     */
    @Column(name = "worn_percent")
    private Integer wornPercent;

    //private Boolean advertisingMode;

    /*first use and premium powerbank - without ad*/

}
