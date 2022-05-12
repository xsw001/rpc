package com.xsw.rpc.transport.netty;

import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.codec.Decoder;
import com.xsw.rpc.enumeration.PackageType;
import com.xsw.rpc.enumeration.RpcError;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CommonDecoder extends ReplayingDecoder<Object> {
    private static final int MAGIC_NUMBER = 0xABCDEF12;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        if (magic != MAGIC_NUMBER) {
            log.error("不识别的协议包: {}", magic);
            throw new IllegalAccessException(RpcError.UNKNOWN_PROTOCOL.getMessage());
        }
        int packageCode = in.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getTypeCode()) {
            packageClass = Request.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getTypeCode()) {
            packageClass = Response.class;
        } else {
            log.error("不识别的数据包: {}", packageCode);
            throw new IllegalAccessException(RpcError.UNKNOWN_PACKAGE_TYPE.getMessage());
        }
        int serializerCode = in.readInt();
        Decoder decoder = Decoder.getByCode(serializerCode);
        if (decoder == null) {
            log.error("不识别的反序列化器: {}", serializerCode);
            throw new IllegalAccessException(RpcError.UNKNOWN_SERIALIZER.getMessage());
        }
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = decoder.decode(bytes, packageClass);
        out.add(obj);
    }
}
