package com.xsw.rpc.register.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.xsw.rpc.Peer;
import com.xsw.rpc.Request;
import com.xsw.rpc.ServiceDescriptor;
import com.xsw.rpc.common.utils.ReflectionUtils;
import com.xsw.rpc.enumeration.RpcError;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.local.ServiceInstance;
import com.xsw.rpc.register.nacos.loadBalance.LoadBalancer;
import com.xsw.rpc.register.nacos.loadBalance.RandomLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    private static final LoadBalancer loadBalancer;

    private static final Set<String> serviceNames;
    private static Peer address;

    private static final String SERVER_ADDRESS = "127.0.0.1:8848";
    private static final NamingService namingService;

    static {
        namingService = getNacosNamingService();
        serviceNames = new HashSet<>();
        loadBalancer = new RandomLoadBalancer();
    }

    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDRESS);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RuntimeException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY.getMessage());
        }
    }

    @Override
    public <T> void register(String serviceName, Peer peer) {
        try {
            logger.info("向注册中心注册服务：" + serviceName);
            namingService.registerInstance(serviceName, peer.getHost(), peer.getPort());
            address = peer;
            serviceNames.add(serviceName);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生: ", e);
            throw new RuntimeException(RpcError.REGISTER_SERVICE_FAILED.getMessage());
        }
    }

    @Override
    public Peer lookup(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            if (instances.isEmpty()) {
                logger.error("服务端全部下线或注册中心未启动，请检查。系统将于 5s 后重试......");
                for (int i = 5; i > 0; i--) {
                    logger.error("......" + i + "......");
                    Thread.sleep(1000);
                }
                logger.error(".....重试.....");
                return lookup(serviceName);
            } else {
                Instance instance = loadBalancer.select(instances);
                return new Peer(instance.getIp(), instance.getPort());
            }
        } catch (NacosException | InterruptedException e) {
            logger.error("获取服务时有错误发生: ", e);
            throw new RuntimeException(RpcError.FETCHING_SERVICE_FAILED.getMessage());
        }
    }

    public static void clearRegistry() {
        if (!serviceNames.isEmpty() && address != null) {
            logger.info("开始注销服务...");
            Integer port = address.getPort();
            String host = address.getHost();
            for (String serviceName : serviceNames) {
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    logger.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }
}
