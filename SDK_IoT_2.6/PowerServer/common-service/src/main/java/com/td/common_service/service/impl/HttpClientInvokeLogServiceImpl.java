package com.td.common_service.service.impl;

import com.td.common_service.mapper.HttpClientInvokeLogMapper;
import com.td.common_service.model.HttpClientInvokeLog;
import com.td.common_service.service.HttpClientInvokeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


/**
 * @author jarrymei
 * @date 20-8-29 下午4:31
 */
@Service
public class HttpClientInvokeLogServiceImpl implements HttpClientInvokeLogService {

    @Autowired
    private HttpClientInvokeLogMapper httpClientInvokeLogMapper;

    /**
     * 异步保存调用日志
     * @param httpClientInvokeLog
     */
    @Async
    @Override
    public void insert(HttpClientInvokeLog httpClientInvokeLog) {
        httpClientInvokeLogMapper.insert(httpClientInvokeLog);
    }
}
