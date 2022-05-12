package com.xsw.rpc.transport.netty;

import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.codec.Encoder;
import com.xsw.rpc.enumeration.PackageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/*
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
 */

public class CommonEncoder extends MessageToByteEncoder<Object> {

    private static final int MAGIC_NUMBER = 0xABCDEF12;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf out) throws Exception {

        out.writeInt(MAGIC_NUMBER);// 4 bytes
        Encoder encoder;
        if (o instanceof Request) { // 4 bytes
            out.writeInt(PackageType.REQUEST_PACK.getTypeCode());
            encoder = Encoder.getByCode(((Request) o).getSerializationId());
        } else {
            out.writeInt(PackageType.RESPONSE_PACK.getTypeCode());
            encoder = Encoder.getByCode(((Response) o).getSerializationId());
        }
        out.writeInt(encoder.getCode()); // 4 bytes
        byte[] bytes = encoder.encode(o);
        out.writeInt(bytes.length); // 4 bytes
        out.writeBytes(bytes); // Length: ${Data Length}
    }
}
