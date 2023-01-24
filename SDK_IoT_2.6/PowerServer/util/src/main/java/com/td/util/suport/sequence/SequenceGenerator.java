package com.td.util.suport.sequence;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 序列生成器，序列值由前缀部分和递增部分共同构成
 * @author chen
 * @version 1.0 2015-11-24
 * @see .说明：当前序列生成器支持按指定序列key的下标(keyIndex)生成并维护相应序列值，使用数组缓存保证线程安全
 */
public class SequenceGenerator {
	private Long[] sequenceCache;
	private Map<Integer, Object> lockMap = new HashMap<Integer, Object>();
	private Object lock = new Object();
	private IGeneratorParams params;
	
	/**
	 * 默认情况：序列递增部分最大值99，不足补零；前缀部分为当前毫秒值。
	 */
	public SequenceGenerator(final int size) {
		this.params = new IGeneratorParams() {
			
			@Override
			public String prefixSequence() {
				return String.valueOf(System.currentTimeMillis());
			}

			@Override
			public Long maxSequence() {
				return 100L;
			}
			
			@Override
			public DecimalFormat format() {
				return new DecimalFormat("00");
			}
		};
		this.sequenceCache = new Long[size];
		//System.out.println(this.hashCode());
	}

	public SequenceGenerator(IGeneratorParams params, final int size) {
		super();
		if(params == null)
			throw new IllegalArgumentException("构造器参数不能为空");
		this.params = params;
		this.sequenceCache = new Long[size];
		//System.out.println("** "+this.hashCode());
	}

	/**
	 * 获取长整形的序列，必须保证序列值为数值型且在长整形范围内，否则该方法将抛出异常
	 * @param keyIndex 当前key在key序列中的数组位置
	 * @return
	 */
	public Long longSequence(Integer keyIndex){
		return Long.parseLong(stringSequence(keyIndex));
	}
	
	/**
	 * 获取字符串型序列, 第1位标识编号是什么用途.方便后台维护和识别. 整个编号最短16位. Long类型最长是19位.
	 * 格式: 编号类型数字1位+系统当前毫秒数13位+1毫秒内最多99个订单2位=16位长的编号
	 * @param keyIndex 当前key在key序列中的数组位置
	 * @return
	 */
	public String stringSequence(Integer keyIndex) {
		Long increment = increment(keyIndex);
		String seq = params.format().format(increment);
		return (keyIndex+1)+params.prefixSequence() + seq;
	}
	
	/**
	 * 自增指定类型的序列，步长为1
	 * @param keyIndex 当前key在key序列中的数组位置
	 * @return
	 */
	private Long increment(Integer keyIndex) {
		int position = keyIndex.intValue();
		if(!lockMap.containsKey(keyIndex)) {
			synchronized (lock) {
				if(!lockMap.containsKey(keyIndex)) {
					sequenceCache[position] = 0L;
					lockMap.put(keyIndex, new Object());
				}
			}
		}
		Object innerLock = lockMap.get(keyIndex);
		synchronized (innerLock) {
			Long current = sequenceCache[position];
//			System.out.println(current+"--"+sequenceKey+"--"+innerLock.hashCode());
			if(current > params.maxSequence()) {
				current = 0L;
			}
			sequenceCache[position] = current + 1;
			return current;
		}
	}
}
