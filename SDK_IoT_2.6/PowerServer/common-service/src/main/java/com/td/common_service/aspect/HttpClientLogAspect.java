package com.td.common_service.aspect;

import com.td.common_service.annotation.HttpClientLog;
import com.td.common_service.model.HttpClientInvokeLog;
import com.td.common_service.service.HttpClientInvokeLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author jarrymei
 * @date 20-8-29 下午5:16
 */
@Aspect
@Component
public class HttpClientLogAspect {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientLogAspect.class);

    @Autowired
    private HttpClientInvokeLogService httpClientInvokeLogService;

    @Pointcut("@annotation(com.td.common_service.annotation.HttpClientLog)")
    public void logPointCut() {
    }

    /**
     * 请求完成后执行
     *
     * @param joinPoint
     * @param jsonResult
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 异常后执行
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        HttpClientLog methodAnnotation = method.getAnnotation(HttpClientLog.class);
        Object[] args = joinPoint.getArgs();
        String url = String.valueOf(args[0]);
        String params = String.valueOf(args[1]);
        String name = method.getName();
        HttpClientInvokeLog httpClientInvokeLog = new HttpClientInvokeLog();
        httpClientInvokeLog.setMethod(method.getName());
        httpClientInvokeLog.setUrl(url);
        httpClientInvokeLog.setParameter(params);
        if (jsonResult != null) {
            httpClientInvokeLog.setResult(String.valueOf(jsonResult));
        }
        if (e != null) {

            httpClientInvokeLog.setException(e.getMessage());
        }
        httpClientInvokeLog.setCreatedTime(new Date());
        httpClientInvokeLogService.insert(httpClientInvokeLog);
    }
}
