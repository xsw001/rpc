package com.xsw.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 表示服务
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDescriptor  implements Serializable {
    /**
     * 待调用接口名称
     */
    private String clazz;
    /**
     * 待调用方法名称
     */
    private String method;
    /**
     * 调用方法的返回值类型
     */
    private String returnType;
    /**
     * 调用方法的参数类型
     */
    private String[] parameterType;

    public static ServiceDescriptor from(Class clazz, Method method) {
        ServiceDescriptor descriptor = new ServiceDescriptor();
        descriptor.setClazz(clazz.getName());
        descriptor.setMethod(method.getName());
        descriptor.setReturnType(method.getReturnType().getName());

        Class<?>[] methodParameterTypes = method.getParameterTypes();
        String[] parameterTypes = new String[methodParameterTypes.length];

        for (int i = 0; i < methodParameterTypes.length; i++) {
            parameterTypes[i] = methodParameterTypes[i].getName();
        }

        descriptor.setParameterType(parameterTypes);
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceDescriptor that = (ServiceDescriptor) o;
        return Objects.equals(clazz, that.clazz) &&
                Objects.equals(method, that.method) &&
                Objects.equals(returnType, that.returnType) &&
                Arrays.equals(parameterType, that.parameterType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clazz, method, returnType);
        result = 31 * result + Arrays.hashCode(parameterType);
        return result;
    }

    @Override
    public String toString() {
        return "ServiceDescriptor{" +
                "clazz='" + clazz + '\'' +
                ", method='" + method + '\'' +
                ", returnType='" + returnType + '\'' +
                ", parameterType=" + Arrays.toString(parameterType) +
                '}';
    }
}
