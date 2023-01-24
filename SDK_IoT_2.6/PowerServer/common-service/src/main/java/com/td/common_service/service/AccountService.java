package com.td.common_service.service;

import com.td.common_service.model.ServiceUser;

public interface AccountService {

    ServiceUser createUserIfEmpty(int lcId);

}
