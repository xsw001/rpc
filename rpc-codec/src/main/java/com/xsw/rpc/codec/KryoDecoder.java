package com.xsw.rpc.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.enumeration.SerializerCode;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoDecoder implements Decoder {

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        kryo.register(Response.class);
        kryo.register(Request.class);
        return kryo;
    });

    @Override
    public <T> T decode(byte[] bytes, Class<T> tClass) {
        try (ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
             Input output = new Input(baos);) {
            Kryo kryo = kryoThreadLocal.get();
            T t = kryo.readObject(output, tClass);
            //Object o = kryo.readClassAndObject(output);
            kryoThreadLocal.remove();

            return t;

        } catch (Exception e) {
            throw new IllegalStateException("反序列化失败", e);
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
