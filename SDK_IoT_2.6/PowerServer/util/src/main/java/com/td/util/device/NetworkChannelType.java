package com.td.util.device;


public enum NetworkChannelType {
    ALIMQTT("Ali_MQTT", 1), NETTYTCP("netty_tcp", 2), AMAZ0NMQTT("Amazon_MQTT", 3);
    private String name;
    private int index;
    // 构造方法
    private NetworkChannelType(String name, int index) {
        this.name = name;
        this.index = index;
    }
    // 普通方法
    public static String getName(int index) {
        for (NetworkChannelType c : NetworkChannelType.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}