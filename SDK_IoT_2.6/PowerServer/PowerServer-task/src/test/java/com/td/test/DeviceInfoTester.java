package com.td.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.td.common_service.mapper.DeviceInfoMapper;
import com.td.common_service.vo.DeviceInfoVO;
import com.td.task.TaskMain;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskMain.class)
public class DeviceInfoTester {

	@Autowired
	DeviceInfoMapper deviceInfoMapper;

	@Test
	public void testMQ() {
		DeviceInfoVO deviceInfoVO = new DeviceInfoVO();
		List<DeviceInfoVO> deviceInfos = deviceInfoMapper.getDeviceList(deviceInfoVO);
		System.out.println(deviceInfos);
	}

}
