package com.td.common.vo.base;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
/**
* @Description:     所有实体需要继承自该类
*/
@Data
@Accessors(chain = true)
public class AbstractBaseVO  implements Serializable {

    private static final long serialVersionUID = 3999442066750191078L;

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 当前页显示数量
     */
    private Integer pageSize = 10;


    /**
     * 主键，自增长
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;


    /**
     * 创建时间，默认当前时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

}
