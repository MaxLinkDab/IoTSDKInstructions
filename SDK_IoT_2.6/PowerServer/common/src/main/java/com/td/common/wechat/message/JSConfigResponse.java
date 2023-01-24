package com.td.common.wechat.message;

import java.util.Date;
import java.util.Map;

import com.td.common.enums.ErrorMsg;
import com.td.common.vo.ResponseMessage;

public class JSConfigResponse extends ResponseMessage {
    
    public JSConfigResponse() {
        super();
    }
    public JSConfigResponse(ErrorMsg err) {
        super(err);
    }
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4264417153759140416L;

    private String appId;       // 必填，公众号的唯一标识
    
    private Map<String, String> body;
    
    private long jsapi_createTime = new Date().getTime();
    
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public Map<String, String> getBody() {
        return body;
    }
    public void setBody(Map<String, String> body) {
        this.body = body;
    }
    public long getJsapi_createTime() {
        return jsapi_createTime;
    }
    public void setJsapi_createTime(long jsapi_createTime) {
        this.jsapi_createTime = jsapi_createTime;
    }
    
}
