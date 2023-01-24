package com.td.common_service.service.impl;

import com.td.common_service.mapper.ServiceUserMapper;
import com.td.common_service.model.ServiceUser;
import com.td.common_service.service.AccountService;
import com.td.common_service.service.LogService;
import com.td.common_service.service.jedis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private LogService logService;

    @Autowired
    private ServiceUserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public ServiceUser createUserIfEmpty(int lcId) {
        //ServiceUser userParam = new ServiceUser();
        //userParam.setLcId(lcId);
        //ServiceUser user = userMapper.selectOne(userParam);
        ServiceUser user = userMapper.getByLcid(lcId);
        if (user == null) {
            user = new ServiceUser();
            user.setLcId(lcId);
            user.setToken(UUID.randomUUID().toString());
            user.setMoney(0);
            user.setMinDeposit(0);
            user.setMessengerLevel(0);
            user.setWebsiteLevel(0);
            user.setApplicationLevel(0);
            userMapper.insert(user);
            user = userMapper.getByLcid(lcId);
            log.info("User " + lcId + " are created" +user.toString());
        }
        return user;
    }

}
