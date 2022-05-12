package com.xsw.rpc.register;

import com.xsw.rpc.Peer;
import com.xsw.rpc.common.annotation.Service;
import com.xsw.rpc.common.annotation.ServiceScan;
import com.xsw.rpc.common.utils.ReflectUtil;
import com.xsw.rpc.common.utils.ReflectionUtils;
import com.xsw.rpc.register.local.LocalServiceProvider;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Set;

@Slf4j
public class AutoRegistration {

    // 管理服务
    private final LocalServiceProvider localServiceProvider;
    // 服务中心
    private final ServiceRegistry serviceRegistry;
    private String host;
    private int port;

    public AutoRegistration(String host, int port) {
        this.host = host;
        this.port = port;
        this.localServiceProvider = new LocalServiceProvider();
        this.serviceRegistry = new NacosServiceRegistry();
    }

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> clazz;
        try {
            clazz = Class.forName(mainClassName);
            if (!clazz.isAnnotationPresent(ServiceScan.class)) {
                log.error("启动类缺少 @ServiceScan 注解");
                throw new RuntimeException("启动类ServiceScan注解缺失");
            }
        } catch (ClassNotFoundException e) {
            log.error("出现未知错误");
            throw new RuntimeException(e);
        }
        String basePackage = clazz.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classes = ReflectUtil.getClasses(basePackage);
        for (Class<?> aClass : classes) {
            if (aClass.isAnnotationPresent(Service.class)) {
                Class<?>[] interfaces = aClass.getInterfaces();
                String[] serviceNames = aClass.getAnnotation(Service.class).name();
                Object instance = ReflectionUtils.newInstance(aClass);
                if (serviceNames.length > 0 && !"".equals(serviceNames[0])) {
                    for (Class<?> anInterface : interfaces) {
                        for (String serviceName : serviceNames) {
                            publishService(anInterface, instance, serviceName);
                        }
                    }
                } else {
                    for (Class<?> anInterface : interfaces) {
                        publishService(anInterface, instance);
                    }
                }
            }
        }
    }

    public <T> void publishService(Class<?> interfaceClass, T bean) {
        localServiceProvider.addServiceProvider(interfaceClass, bean);
        serviceRegistry.register(interfaceClass.getCanonicalName(), new Peer(host, port));
    }

    public <T> void publishService(Class<?> interfaceClass, T bean, String method) {
        localServiceProvider.addServiceProvider(interfaceClass, bean, method);
        serviceRegistry.register(interfaceClass.getCanonicalName(), new Peer(host, port));
    }
}
