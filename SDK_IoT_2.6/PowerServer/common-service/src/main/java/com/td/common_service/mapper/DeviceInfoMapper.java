package com.td.common_service.mapper;

import com.td.common.mapper.base.BaseMapper;
import com.td.common_service.model.DeviceInfo;
import com.td.common_service.vo.DeviceInfoVO;
import com.td.common_service.vo.DevicePowerBankInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeviceInfoMapper extends BaseMapper<DeviceInfo> {

	DeviceInfo selectDeviceInfoByUuId(String uuid);

	int updateByDeviceUuidSelective(DeviceInfo deviceInfo);

	int updateState(String deviceNo, Integer deviceState);

    int deleteByDeviceUuid(String deviceUuid);
    int deleteByPrimaryKey(Integer id);

    int insert(DeviceInfo record);

    int insertSelective(DeviceInfo record);

    DeviceInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeviceInfo record);

    int updateByPrimaryKey(DeviceInfo record);

    int updateVersionByDeviceUuidSelective(DeviceInfo deviceInfo);

    DeviceInfo  getDeviceInfoByCloudId(String cloudId);

    List<DeviceInfo> queryDeviceList(DeviceInfo deviceInfo);

    List<DeviceInfoVO> getDeviceList(DeviceInfoVO deviceInfo);

    List<DeviceInfo> selectByDynaParams(Map<String ,Object> params);

    int deleteByDeviceNo(String deviceNo);

    List<DevicePowerBankInfoVO> selectDetailInfoByDeviceUuid(@Param("deviceUuid") String deviceUuid);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    int updateByIdsSelective(@Param("soleUid")String soleId, @Param("placeUid")String placeId, @Param("list") List<Integer> idList);//设置设备 agent_no sole_id place_id

    List<DeviceInfoVO> selectDeviceInfoIsNotSoleUid(String deviceInfo);

    List<DeviceInfoVO> selectDeviceInfoIsNotPlaceUid(String deviceInfo);

    /**
     *  主机硬件需要升级的设备
     */
    List<DeviceInfo> selectDeviceInfoByHardVersion(String versionInfo);
    /**
     *  从机协议需要升级的设备
     */
    List<DeviceInfo> selectDeviceInfoByAgreementVersion(String versionInfo);

    int updateNettyDeviceToOffline();

    /**
     *  定时-昨日设备总金额
     */
    List<DeviceInfo> selectDeviceInfoDaySumMoney();
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Select("SELECT hard_version FROM device_info WHERE  device_uuid= #{uuid}")
    String getHardVersion(@Param("uuid") String uuid);
}
