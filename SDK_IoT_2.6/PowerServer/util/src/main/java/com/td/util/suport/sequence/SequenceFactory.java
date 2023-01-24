package com.td.util.suport.sequence;

import java.text.DecimalFormat;

import com.td.util.DateHelper;

/**
 * 序列生成器工厂
 *  @author chen
 * @version 1.0 2015-11-24
 */
public class SequenceFactory {

	/**
	 * 获取序列生成器
	 * @param type	序列生成器类型
	 * @param size	序列key总数
     * @return
     */
	public static SequenceGenerator getGenerator(GeneratorType type, final int size) {
		if(type == GeneratorType.dateFormatGenerator) {
			return new SequenceGenerator(new IGeneratorParams() {
				
				@Override
				public String prefixSequence() {
					return DateHelper.formatNoDelimiters(System.currentTimeMillis());
				}

				@Override
				public Long maxSequence() {
					return 99999L;
				}
				
				@Override
				public DecimalFormat format() {
					return new DecimalFormat("00000");
				}
			}, size);
		}
		else {
			return new SequenceGenerator(size);
		}
	}
	
	public enum GeneratorType {
		/**
		 * 格式：14483678093350000<br/>
		 * 说明：序列递增部分最大值9999，不足补零；前缀部分为当前毫秒值，格式1448367809335。
		 */
		defaultGenerator, 
		/**
		 * 格式：2015112421242700000<br/>
		 * 说明：序列递增部分最大值99999，不足补零；前缀部分为当前日期及时间，格式yyyyMMddHHmmss。
		 */
		dateFormatGenerator, 
		;
	}
}
