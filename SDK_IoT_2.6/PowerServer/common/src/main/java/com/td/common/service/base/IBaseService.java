package com.td.common.service.base;

import com.td.common.mapper.base.BaseMapper;
import com.td.common.model.base.BaseModel;

/**
 * 
 * @author Administrator
 *
 * @param <Model>
 * @param <VO>
 * @param <Mapper>
 */
public interface IBaseService<Model extends BaseModel, VO, Mapper extends BaseMapper<Model>> {

	/**
	 * 
	 * @param model
	 * @return
	 */
	public int insert(Model model);

}
