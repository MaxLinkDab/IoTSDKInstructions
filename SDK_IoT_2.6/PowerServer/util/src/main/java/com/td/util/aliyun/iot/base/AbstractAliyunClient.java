/*
 * Copyright (c) 2014-2016 Alibaba Group. All rights reserved.
 * License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.td.util.aliyun.iot.base;

import com.aliyuncs.AcsResponse;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.RpcAcsRequest;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.td.util.config.AliyunConfig;

public abstract class AbstractAliyunClient {

	private static DefaultAcsClient client;

	protected static /* final */byte testFlag = 0;
	static {
		if (testFlag == 1) {
			AliyunConfig.ACCESS_KEY_ID = "LTAI5tLg1fSDbppNRKR3Znzr";
			AliyunConfig.ACCESS_KEY_SECRET = "eK9SoAT4MuDM5UynmTDywxLZ12pL6u";
			AliyunConfig.REGION_ID = "cn-shanghai";
			AliyunConfig.PRODUCT_CODE = "Iot";
			AliyunConfig.DOMAIN = "iot.cn-shanghai.aliyuncs.com";
			AliyunConfig.VERSION = "2018-01-20";
			AliyunConfig.UID = "862506049385829";
			AliyunConfig.PRODUCT_KEY = "a1fe2TxOa3N";
			
			client = getDefaultAcsClient(); // for main test .
		}
	}

	private static DefaultAcsClient getDefaultAcsClient() {
		DefaultAcsClient client = null;

		try {
			IClientProfile profile = DefaultProfile.getProfile(AliyunConfig.REGION_ID,
					AliyunConfig.ACCESS_KEY_ID, AliyunConfig.ACCESS_KEY_SECRET);
			DefaultProfile.addEndpoint(AliyunConfig.REGION_ID, AliyunConfig.REGION_ID,
					AliyunConfig.PRODUCT_CODE, AliyunConfig.DOMAIN);
			// 初始化client
			client = new DefaultAcsClient(profile);
		} catch (Exception e) {
			System.err.println("初始化client失败！exception:" + e.toString());
		}

		return client;
	}

	private static DefaultAcsClient getClient() {
		if (client == null) {
			client = getDefaultAcsClient();
		}
		return client;
	}

	/**
	 * 接口请求地址 action 接口名称 domain 线上地址 version 接口版本
	 */
	public /* static */CommonRequest executeTests(String action) {
		CommonRequest request = new CommonRequest();
		request.setDomain(AliyunConfig.DOMAIN);
		request.setMethod(MethodType.POST);
		request.setVersion(AliyunConfig.VERSION);
		request.setAction(action);

		return request;
	}

	// @SuppressWarnings({ "unchecked", "rawtypes" })
	public static AcsResponse executeTest(RpcAcsRequest request) {
		AcsResponse response = null;
		try {
			response = getClient().getAcsResponse(request);
		} catch (Exception e) {
			System.err.println("执行失败：e:" + e.getMessage());
		}
		return response;
	}

}
