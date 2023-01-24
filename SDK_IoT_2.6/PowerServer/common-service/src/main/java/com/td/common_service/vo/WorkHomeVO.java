package com.td.common_service.vo;

import com.td.common.vo.base.AbstractBaseVO;
import lombok.Data;

@Data
public class WorkHomeVO extends AbstractBaseVO {

    private String orderNo;
    private Integer userId;
    private String powerNo;
    private Integer money;
    private String deviceUuid;


    private Integer agentDivide;
    private Integer soleDivide;
    private Integer placeDivide;

}
