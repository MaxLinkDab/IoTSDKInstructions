package com.td.common_service.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 任务接口抽象实现，支持状态管理，建议所有定时任务继承自该类
 * 
 * @author haifei
 * @version 1.0 2015-10-17
 */
public abstract class AbstractTask implements IBaseTask {
	private Logger log = LoggerFactory.getLogger(AbstractTask.class);
	
	/**
	 * 上次执行时间
	 */
	private long lastRunTime;

	/**
	 * 任务逻辑
	 */
	public abstract void handle();
	
	/**
	 * 任务入口。除非上次任务已完成，否则跳过本次执行
	 * @throws Exception 
	 */
	@Override
	public void run() throws Exception {
		if (stoped()) {
			return;
		}
		if(runnable()) {
			running();
			lastRunTime = System.currentTimeMillis();
			try {
				handle();
			} catch (Exception e) {
				throw e;
			} finally {
				complete();
				printElapsed();
			}
		}
		else {
			log.info("[{}]skip running.", getTaskName());
		}
	}
	
	/**
	 * 打印任务耗时
	 */
	protected void printElapsed() {
		if(!"FALSE".equals(getTaskName() ))
			log.info("[{}]耗时{}ms.", getTaskName(), (System.currentTimeMillis() - lastRunTime));
	}

	/////////////////////////////////////////////////////////////////////////////
	private TaskStatus status = TaskStatus.Runable;
	
	/**
	 * 可否运行
	 */
	protected boolean runnable() {
		return ((status == TaskStatus.Runable) && (status != TaskStatus.Stop));
	}

	/**
	 * 运行中
	 */
	protected void running() {
		if(status != TaskStatus.Stop) {
			status = TaskStatus.Running;
		}
	}

	/**
	 * 运行结束
	 */
	protected void complete() {
		if(status != TaskStatus.Stop) {
			status = TaskStatus.Runable;
		}
	}
	
	/**
	 * 已经停止
	 * @return
	 */
	protected boolean stoped() {
		return (status == TaskStatus.Stop);
	}

	/**
	 * 任务状态
	 */
	public enum TaskStatus {
		/**可运行*/
		Runable,
		/**运行中 */
		Running,
		/**已停止 */
		Stop, ;
	}
	
	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}
}
