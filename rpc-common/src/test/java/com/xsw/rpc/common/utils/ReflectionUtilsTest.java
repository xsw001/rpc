package com.xsw.rpc.common.utils;

import junit.framework.TestCase;

import java.lang.reflect.Method;

public class ReflectionUtilsTest extends TestCase {

    public void testNewInstance() {
        TestClass testClass = ReflectionUtils.newInstance(TestClass.class);
        System.out.println(testClass.b());
    }

    public void testGetPublicMethods() {
        Method[] methods = ReflectionUtils.getPublicMethods(TestClass.class);
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }

    public void testInvoke() {
        Method[] methods = ReflectionUtils.getPublicMethods(TestClass.class);
        Method method = methods[0];
        Object invoke = ReflectionUtils.invoke(new TestClass(), method);
        assertEquals("b", invoke);
    }
}