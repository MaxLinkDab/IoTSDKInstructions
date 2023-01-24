package com.td.common.service.impl.base;

import org.springframework.beans.factory.annotation.Autowired;

import com.td.common.mapper.base.BaseMapper;
import com.td.common.model.base.BaseModel;
import com.td.common.service.base.IBaseService;

/**
 * 
 * @author Administrator
 *
 * @param <Model>
 * @param <VO>
 * @param <Mapper>
 */
public abstract class BaseServiceImpl<Model extends BaseModel, VO, Mapper extends BaseMapper<Model>> implements IBaseService<Model, VO, Mapper> {

	@Autowired
	protected Mapper mapper;
	
	@Override
	public int insert(Model model) {
		return mapper.insert(model);
	}

}
