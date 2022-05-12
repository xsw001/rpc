package com.xsw.rpc.register.local;

import com.xsw.rpc.Request;
import com.xsw.rpc.ServiceDescriptor;
import com.xsw.rpc.common.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LocalServiceProvider {

    // static final修饰的属性只被赋值一次，初始化后不可修改，所有对象的值相同
    private static final Map<ServiceDescriptor, ServiceInstance> services = new ConcurrentHashMap<>();

    public void addServiceProvider(Class<?> interfaceClass, Object service) {
        check(interfaceClass, service);
        Method[] methods = ReflectionUtils.getPublicMethods(interfaceClass);
        for (Method method : methods) {
            ServiceInstance instance = new ServiceInstance(service, method);
            ServiceDescriptor descriptor = ServiceDescriptor.from(interfaceClass, method);
            System.out.println(descriptor.toString());
            services.put(descriptor, instance);
            log.info("注册服务 : {} {}", descriptor.getClazz(), descriptor.getMethod());
        }
    }

    public void addServiceProvider(Class<?> interfaceClass, Object service, String methodName) {
        check(interfaceClass, service);
        Method[] methods = ReflectionUtils.getPublicMethods(interfaceClass);
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                ServiceInstance instance = new ServiceInstance(service, method);
                ServiceDescriptor descriptor = ServiceDescriptor.from(interfaceClass, method);
                System.out.println(descriptor.toString());
                services.put(descriptor, instance);
                log.info("注册服务 : {} {}", descriptor.getClazz(), descriptor.getMethod());
            }
        }
    }

    private void check(Class<?> interfaceClass, Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        HashSet<Class<?>> classes = new HashSet<>(Arrays.asList(interfaces));
        if (!classes.contains(interfaceClass)) {
            log.error("接口类型不一致");
            throw new RuntimeException("接口类型不一致");
        }
    }

    public ServiceInstance getServiceProvider(Request request) {
        return services.get(request.getService());
    }
}

