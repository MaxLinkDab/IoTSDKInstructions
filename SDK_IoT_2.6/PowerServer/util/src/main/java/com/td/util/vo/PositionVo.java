package com.td.util.vo;

/**
 * @author jarrymei
 * @date 20-3-18 下午5:57
 */
public class PositionVo {

    private String positionUuid;
    private String machineUuid;

    public PositionVo(String positionUuid, String machineUuid) {
        this.positionUuid = positionUuid;
        this.machineUuid = machineUuid;
    }

    public String getPositionUuid() {
        return positionUuid;
    }

    public void setPositionUuid(String positionUuid) {
        this.positionUuid = positionUuid;
    }

    public String getMachineUuid() {
        return machineUuid;
    }

    public void setMachineUuid(String machineUuid) {
        this.machineUuid = machineUuid;
    }
}
