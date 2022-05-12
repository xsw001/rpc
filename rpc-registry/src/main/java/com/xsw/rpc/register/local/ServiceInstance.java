package com.xsw.rpc.register.local;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 表示一个具体服务
 */
@Data
@AllArgsConstructor
public class ServiceInstance {
    // 哪个对象提供的
    private Object target;
    // 对象的方法报漏为服务
    private Method method;
}
