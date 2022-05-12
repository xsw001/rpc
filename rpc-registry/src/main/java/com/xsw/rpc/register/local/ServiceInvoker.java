package com.xsw.rpc.register.local;
import com.xsw.rpc.Request;
import com.xsw.rpc.common.utils.ReflectionUtils;

/**
 * 调用 ServiceInstance 的辅助类
 * 调用具体服务
 */

public class ServiceInvoker {

    public Object invoke(ServiceInstance service, Request request) {
        // System.out.println(service.getMethod().getName() + "要执行了---");
        return ReflectionUtils.invoke(service.getTarget(), service.getMethod(), request.getParameters());
    }
}

