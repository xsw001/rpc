package com.xsw.rpc.server;

import com.xsw.rpc.Request;
import com.xsw.rpc.ServiceDescriptor;
import com.xsw.rpc.common.utils.ReflectionUtils;
import com.xsw.rpc.register.local.ServiceInstance;
import com.xsw.rpc.register.local.LocalServiceProvider;
import junit.framework.TestCase;

import java.lang.reflect.Method;

public class LocalServiceProviderTest extends TestCase {
    LocalServiceProvider sm = new LocalServiceProvider();


    public void testRegister() {
        TestInterface aClass = new TestClass();
        sm.addServiceProvider(TestInterface.class, aClass);
    }

    public void testLookup() {

        TestInterface aClass = new TestClass();
        sm.addServiceProvider(TestInterface.class, aClass);

        Method[] methods = ReflectionUtils.getPublicMethods(TestInterface.class);
        ServiceDescriptor descriptor = ServiceDescriptor.from(TestInterface.class, methods[0]);
        Request request = new Request();
        request.setService(descriptor);
        System.out.println(request);

        ServiceInstance serviceInstance = sm.getServiceProvider(request);
        System.out.println(serviceInstance);
    }
}