package com.td.common_service.task;


/**
 * 任务接口
 * 
 * @author haifei
 * @version 1.0 2015-10-17
 */
public interface IBaseTask {
	
	/**
	 * 执行任务
	 */
	void run() throws Exception;
	
	/**
	 * 任务名称
	 */
	String getTaskName();
}
