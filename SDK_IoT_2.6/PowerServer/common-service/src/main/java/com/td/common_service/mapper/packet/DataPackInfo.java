package com.td.common_service.mapper.packet;

import com.td.util.HexToBinaryUtils;
import lombok.Data;

@Data
public class DataPackInfo {
    private String dataLen ;
    private String machineUuid;
    private String newBack ;
    private Integer dataL ;
    private String subAllPos;
    private String checkCode;
    private String code;

    public DataPackInfo(final String commandData){
        checkCode = HexToBinaryUtils.getCheckCode(commandData.substring(0, commandData.length() - 2)); //计算校验
        code = commandData.substring(commandData.length() - 2, commandData.length()); //校验码
        dataLen = commandData.substring(4, 6); //数据长度
        machineUuid = commandData.substring(6,8);//从机id(数据为uuid)
        newBack = commandData.substring(8, 10); //新归还事件
        dataL = HexToBinaryUtils.getDecimal(dataLen);
        subAllPos = commandData.substring(10, commandData.length() - 2); // 仓位信息
    }
}
