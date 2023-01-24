package com.td.common_service.vo;

import java.util.Date;
import java.util.List;

import com.td.common.vo.base.AbstractBaseVO;

import lombok.Data;

@Data
public class DeviceInfoVO extends AbstractBaseVO {

    private String createdTime;

    private String deviceNo;

    private String deviceName;

    private String cloudId;

    private String iccId;

    private String deviceKey;

    private String sn;

    /**
     *  (0禁用1启动)
     */
    private Integer deviceState;

    private Long trace;

    private Integer spaceNu;

    private Integer machineNu;

    private String deviceUuid;

    private String softVersion;

    private String hardVersion;

    private String agreementVersion;

    private String url;

    private String deviceModel;//设备型号 6口机、12口机

    private String deviceSignal;//信号
    private String networkType;//网络机制（2G、3G、4G）
    private String networkOperator;//网络运营商

    private String deviceIP;

    private String agentName; //代理商名称
    private String soleName;//销售代表名称
    private String placeName; //场所名称


    private String agentNo;
    private String soleUid; //销售代表
    private String placeUid;//场所管理员
    private String agentUid;//代理商


    private String ids;
    private List<Integer> idList;
}