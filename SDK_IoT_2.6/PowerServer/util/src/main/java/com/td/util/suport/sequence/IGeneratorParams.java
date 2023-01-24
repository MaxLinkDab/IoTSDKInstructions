package com.td.util.suport.sequence;

import java.text.DecimalFormat;

public interface IGeneratorParams {
	/**
	 * 序列递增部分的最大值
	 * @return
	 */
	Long maxSequence();
	
	/**
	 * 序列递增部分的格式
	 * @return
	 */
	DecimalFormat format();
	
	/**
	 * 序列的前缀部分
	 * @return
	 */
	String prefixSequence();

}
