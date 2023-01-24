package com.td.common_service.annotation;

import java.lang.annotation.*;

/**
 * @author jarrymei
 * @date 20-8-1 下午9:39
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpClientLog {
}
