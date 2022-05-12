package com.xsw.rpc.common.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class NacosUtils {

    private static Set<String> serviceNames;
    private static InetSocketAddress address;
    private static NamingService namingService;

    private static final String SERVER_ADDRESS = "127.0.0.1:8848";

    static {
        namingService = getNacosNamingService();
        serviceNames = new HashSet<>();
    }

    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDRESS);
        } catch (NacosException e) {
            log.error("连接到Nacos时有错误发生: ", e);
            throw new RuntimeException("连接注册中心失败");
        }
    }

    public static void clearRegistry() {

        if (!serviceNames.isEmpty() && address != null) {

        }
    }
}
