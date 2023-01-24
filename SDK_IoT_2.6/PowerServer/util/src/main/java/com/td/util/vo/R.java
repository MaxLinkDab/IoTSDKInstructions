package com.td.util.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: sherlock
 * Date: 2018-11-28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> {
    /**
     * 错误码
     */
    private int code;

    /**
     * 返回说明
     */
    private String msg;

    /**
     * 具体数据
     */
    private T data;

    /**
     * 总数
     */
    private Long count;
}