package org.myan.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import secure.SecureChatSslContextFactory;

import javax.net.ssl.SSLEngine;


/**
 * Created by myan on 2017/10/26.
 * Intellij IDEA
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (HttpServer.isSSL()) {
            SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            pipeline.addLast("ssl", new SslHandler(engine));
        }

        // add request decoder
        pipeline.addLast("decoder", new HttpRequestDecoder());
        // add response encoder
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // compress http content
        pipeline.addLast("deflater", new HttpContentCompressor());
        // add our handler
        pipeline.addLast("handler", new ServerHandler());
    }

}
