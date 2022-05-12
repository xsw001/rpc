package com.xsw.rpc.register.nacos.hook;

import com.xsw.rpc.common.utils.ThreadPoolFactoryUtil;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@Slf4j
public class ShutdownHook {

    private final static ShutdownHook shutdown = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdown;
    }

    public void addClearRegistryHook() {
        log.info("服务端下线后自动注销所有服务。");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosServiceRegistry.clearRegistry();
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}
