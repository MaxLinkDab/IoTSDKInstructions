package com.td.common_service.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.td.common.mapper.base.BaseMapper;
import com.td.common_service.model.Powerbank;

import java.util.List;

@Mapper
public interface PowerbankMapper extends BaseMapper<Powerbank> {

//  @Select("SELECT * FROM powerbank WHERE power_ad = (SELECT MAX(power_ad) FROM powerbank  WHERE  state = 0) AND device_uuid= #{uuid} LIMIT 0,1")
	@Select("SELECT * FROM powerbank WHERE state = 0 and error_state = 0 AND device_uuid= #{uuid} order by power_ad desc LIMIT 0,1")
	Powerbank selectMaxPowerBank(@Param("uuid") String uuid);

	@Select("SELECT * FROM powerbank WHERE device_uuid= #{uuid} ORDER BY position_uuid ")
	List<Powerbank> selectAllByDeviceUuid(@Param("uuid") String uuid);

	@Select("SELECT * FROM powerbank WHERE device_uuid= #{uuid} AND power_no != 'C000' AND state = 0 ORDER BY machine_uuid ")
	List<Powerbank> selectPowerbankByDeviceUuid(@Param("uuid") String uuid);

	int deleteByDeviceUuid(@Param("deviceUuid") String deviceUuid);

	@Select("SELECT p.* FROM powerbank p ,device_info d WHERE d.place_uid = #{placeUid} AND d.device_uuid = p.device_uuid AND p.state = #{state} AND error_state =0")
	List<Powerbank> getPoweState(@Param("state") int state,@Param("placeUid") int placeUid);

	@Select("SELECT * FROM powerbank WHERE state = 0 and error_state = 0 AND device_uuid= #{uuid} And id != #{id} order by power_ad desc LIMIT 0,1")
	Powerbank selectPowerBank(@Param("uuid") String uuid,@Param("id") int id);

	@Select("SELECT count(1) c from device_info d left join powerbank p on d.device_uuid=p.device_uuid  where d.device_uuid=#{uuid} and p.state=0")
	int getRemainingPower(@Param("uuid") String uuid);

	@Select("SELECT * from powerbank WHERE state = 0 and error_state = 0 AND device_uuid= #{uuid} AND position_uuid= #{positionId} AND machine_uuid=#{machineUuid}")
	Powerbank selectPowerBankByUuidAndPositionId(@Param("uuid") String uuid, @Param("positionId") String positionId, @Param("machineUuid") String machineUuid);

	@Select("SELECT * from powerbank WHERE state = 0 and error_state = 0 AND device_uuid= #{uuid} AND all_position_uuild= #{allPositionUuid} AND machine_uuid=#{machineUuid}")
	Powerbank selectPowerBankByUuidAndAllPositionUuid(@Param("uuid") String uuid, @Param("allPositionUuid") String allPositionUuid, @Param("machineUuid") String machineUuid);

	@Select("SELECT * FROM powerbank WHERE state = 0 AND charging_switch = 0 AND device_uuid= #{uuid} AND machine_uuid = #{machineUuid} AND power_ad BETWEEN 0 AND 12 order by power_ad desc")
	List<Powerbank> selectPowerBank4Ad1(@Param("uuid") String uuid,String machineUuid);

	@Select("SELECT * FROM powerbank WHERE state = 0 AND charging_switch = 0 AND device_uuid= #{uuid} AND machine_uuid = #{machineUuid} AND power_ad BETWEEN 13 AND 14 order by power_ad desc")
	List<Powerbank> selectPowerBank4Ad2(@Param("uuid") String uuid,String machineUuid);

	@Select("SELECT * FROM powerbank WHERE state = 0 AND charging_switch = 1 AND device_uuid= #{uuid} order by power_ad ASC")
	List<Powerbank> selectPowerBank4Ad3(@Param("uuid") String uuid);

	@Select("SELECT * FROM powerbank WHERE state = 0 AND charging_switch = 0 AND device_uuid= #{uuid} AND power_ad BETWEEN 0 AND 10 order by position_uuid ASC")
	List<Powerbank> selectPowerBank4Ad4(@Param("uuid") String uuid,String machineUuid);

	@Select("SELECT count(*) FROM powerbank WHERE state = 0 AND charging_switch = 0 AND device_uuid= #{uuid} AND power_ad != 15 order by power_ad DESC")
	int selectPowerBankCount(@Param("uuid") String uuid,String machineUuid);

	@Select("SELECT * FROM powerbank WHERE state = 0 AND charging_switch = 0 AND device_uuid= #{uuid} AND power_ad = 15 order by power_ad DESC")
	List<Powerbank> selectPowerBank4Ad5(@Param("uuid") String uuid,String machineUuid);
}
