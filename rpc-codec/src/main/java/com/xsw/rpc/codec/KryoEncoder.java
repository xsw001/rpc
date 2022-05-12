package com.xsw.rpc.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.enumeration.SerializerCode;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

@Slf4j
public class KryoEncoder implements Encoder {
    // 因为 Kryo 不是线程安全的，所以使用 ThreadLocal 来存储 Kryo 对象

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(
                new StdInstantiatorStrategy()));
        kryo.setRegistrationRequired(false);//关闭注册行为
        kryo.setReferences(true);//支持循环引用
        kryo.register(Response.class);
        kryo.register(Request.class);
        return kryo;
    });

    @Override
    public byte[] encode(Object obj) {
        try (ByteArrayOutputStream by = new ByteArrayOutputStream();
             Output output = new Output(by)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            // Object->byte:将对象序列化为byte数组
            // kryo.writeClassAndObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            log.warn("序列化失败!");
            throw new IllegalStateException("Serialization failed", e);
        }
    }

    /*public static void main(String[] args) {
        System.out.println(Arrays.toString(en(CalcService.class)));
    }

    public static byte[] en(Object obj) {
        try (ByteArrayOutputStream by = new ByteArrayOutputStream();
             Output output = new Output(by)) {
            Kryo kryo = new Kryo();
            //kryo.writeObject(output, obj);// Object->byte:将对象序列化为byte数组
            kryo.writeClassAndObject(output, obj);
            System.out.println(Arrays.toString(by.toByteArray()));
            System.out.println(Arrays.toString(output.toBytes()));
            return by.toByteArray();
        } catch (Exception e) {
            log.warn("序列化失败!");
            throw new IllegalStateException("Serialization failed", e);
        }
    }

    public static class CalcService {
        int add(int a, int b) {
            return a + b;
        }
    }*/


    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
