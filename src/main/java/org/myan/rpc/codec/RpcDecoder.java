package org.myan.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by myan on 2017/10/17.
 * Intellij IDEA
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < 4)
            return;
        byteBuf.markReaderIndex();
        int len = byteBuf.readInt();
        if(len < 0)
            ctx.close();
        if(byteBuf.readableBytes() < len) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[len];
        byteBuf.readBytes(data);

        Object obj = SerializationUtil.deserialize(data, genericClass);
        list.add(obj);
    }
}
