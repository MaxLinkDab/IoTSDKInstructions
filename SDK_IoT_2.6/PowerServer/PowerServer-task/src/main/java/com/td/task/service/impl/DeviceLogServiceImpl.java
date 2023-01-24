package com.td.task.service.impl;

import org.springframework.stereotype.Service;

import com.td.common.service.impl.base.BaseServiceImpl;
import com.td.task.mapper.DeviceLogMapper;
import com.td.task.model.DeviceLog;
import com.td.task.service.IDeviceLogService;

@Service
public class DeviceLogServiceImpl extends BaseServiceImpl<DeviceLog, DeviceLog, DeviceLogMapper> implements IDeviceLogService {
}
