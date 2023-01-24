package com.td.common.mapper.base;

import com.td.common.model.base.BaseModel;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 
 * @author Administrator
 *
 * @param <Model>
 */
public interface BaseMapper<Model extends BaseModel> extends Mapper<Model>, MySqlMapper<Model> {

}
