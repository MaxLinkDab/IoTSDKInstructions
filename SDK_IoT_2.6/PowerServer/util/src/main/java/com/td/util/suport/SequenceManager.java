package com.td.util.suport;

import com.td.util.suport.sequence.SequenceFactory;
import com.td.util.suport.sequence.SequenceFactory.GeneratorType;
import com.td.util.suport.sequence.SequenceGenerator;

/**
 * 序列管理器
 *
 * @author chen
 * @version 1.0 2015-11-24
 */
public class SequenceManager {
    private static SequenceGenerator defaultGenerator = SequenceFactory.getGenerator(GeneratorType.defaultGenerator,
            SequenceKey.values().length);

    /**
     * 创建用户编号
     *
     * @return
     */
    public static Long createUserNo() {
        return defaultGenerator.longSequence(SequenceKey.UserNo.ordinal());
    }

    /**
     * 创建设备编号
     *
     * @return
     */
    public static Long createDeviceNo() {
        return defaultGenerator.longSequence(SequenceKey.DeviceNo.ordinal());
    }

    /**
     * 创建充电宝编号
     *
     * @return
     */
    public static Long createPowerNo() {
        return defaultGenerator.longSequence(SequenceKey.PowerNo.ordinal());
    }


    /**
     * 创建仓位编号
     *
     * @return
     */
    public static Long createPositionNo() {
        return defaultGenerator.longSequence(SequenceKey.PositionNo.ordinal());
    }


    /**
     * 创建支付编号
     *
     * @return
     */
    public static Long createPayNo() {
        return defaultGenerator.longSequence(SequenceKey.PayNo.ordinal());
    }


    /**
     * 创建订单编号
     *
     * @return
     */
    public static Long createOrderNo() {
        return defaultGenerator.longSequence(SequenceKey.OrderNo.ordinal());
    }


    /**
     * 创建套餐编号
     *
     * @return
     */
    public static Long createChargeNo() {
        return defaultGenerator.longSequence(SequenceKey.chargeNo.ordinal());
    }

    /**
     * 创建代理商编号
     *
     * @return
     */
    public static Long createAgentNo() {
        return defaultGenerator.longSequence(SequenceKey.agentNo.ordinal());
    }

    /**
     * 创建场所编号
     *
     * @return
     */
    public static Long createPlaceNo() {
        return defaultGenerator.longSequence(SequenceKey.placeNo.ordinal());
    }

    /**
     * 创建广告资源编号
     *
     * @return
     */
    public static Long createAdvertResNo() {
        return defaultGenerator.longSequence(SequenceKey.advertResNo.ordinal());
    }

    /**
     * 创建广告发布编号
     *
     * @return
     */
    public static Long createAdvertPushNo() {
        return defaultGenerator.longSequence(SequenceKey.advertPushNo.ordinal());
    }
}
