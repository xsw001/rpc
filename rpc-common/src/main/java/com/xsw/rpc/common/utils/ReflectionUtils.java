package com.xsw.rpc.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * 反射工具类
 */
@Slf4j
public class ReflectionUtils {

    /**
     * 根据class创建对象
     *
     * @param clazz 待创建对象的类
     * @param <T>   对象类型
     * @return 创建好的对象
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            log.error("创建 " + clazz + " 时有错误发生");
            throw new IllegalStateException(e);
        }
    }

    /**
     * 获取公共方法
     *
     * @param clazz 对象的类
     * @return 当前类的公共方法
     */
    public static Method[] getPublicMethods(Class clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        ArrayList<Method> list = new ArrayList<>();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()))
                list.add(method);
        }
        return list.toArray(new Method[0]);
    }

    /**
     * 调用指定对象的指定方法
     * @param obj 被调用方法的对象
     * @param method 被调用的方法
     * @param args 餐数
     * @return  返回值
     */
    public static Object invoke(Object obj, Method method, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
