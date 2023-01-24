package com.td.common.vo;


import lombok.Data;

/**
 * 客户端请求消息
 *
 * @author chen
 * @version 1.0 2015-9-18
 */
@Data
public class RequestMessage extends Message {
    private static final long serialVersionUID = -4938542433249518107L;


    /**
     * 用户编号
     */
    private Long userNo;

    /**
     * 平台自身token
     */
    private String token;


}
