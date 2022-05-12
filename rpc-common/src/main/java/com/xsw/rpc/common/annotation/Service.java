package com.xsw.rpc.common.annotation;

import java.lang.annotation.*;

/**
 * 放在一个类上，标识这个类提供一个服务
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    // Service 注解的值定义为该服务的名称，默认值是该类的完整类名
    public String[] name() default "";
}
