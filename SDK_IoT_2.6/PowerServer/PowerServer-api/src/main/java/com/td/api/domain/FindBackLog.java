package com.td.api.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FindBackLog {
    private Integer id;

    private String deviceUuid;

    private String machineUuid;

    private String event;

    private Integer bid;

    private Date createdTime;

    private String startTime;
    private String endTime;
}