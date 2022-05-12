package com.xsw.rpc.register;

import com.xsw.rpc.Peer;
import com.xsw.rpc.Request;
import com.xsw.rpc.register.local.ServiceInstance;
import org.python.antlr.ast.Str;

/**
 * 管理RPC暴露的服务
 * 注册服务
 * 查找服务
 */
public interface ServiceRegistry {
    <T> void register(String serviceName, Peer peer);

    Peer lookup(String serviceName);
}
