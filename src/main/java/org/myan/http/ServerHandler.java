package org.myan.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaderNames.COOKIE;

/**
 * Created by myan on 2017/10/26.
 * Intellij IDEA
 */
public class ServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private HttpRequest request;
    private boolean readingChunks;
    private final StringBuilder responseContent = new StringBuilder();
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk
    private HttpPostRequestDecoder decoder;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null)
            decoder.cleanFiles();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        messageReceived(ctx, msg);
    }

    private void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws URISyntaxException {
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            URI uri = new URI(request.uri());

            if (uri.getPath().equals("/favicon.ico"))
                return; // do nothing
            if (uri.getPath().equals("/")) {
                writeMenu(ctx);
                return;
            }

            // build the response
            responseContent.setLength(0);
            responseContent.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
            responseContent.append("===================================\r\n");
            responseContent.append("VERSION: ").append(request.protocolVersion().text()).append("\r\n");
            responseContent.append("REQUEST_URI: ").append(request.uri()).append("\r\n\r\n");
            responseContent.append("\r\n\r\n");
            for (Map.Entry<String, String> entry : request.headers()) {
                responseContent.append("HEADER: ").append(entry.getKey()).append('=').
                        append(entry.getValue()).append("\r\n");
            }
            responseContent.append("\r\n\r\n");

            // cookie part
            String value = request.headers().get(COOKIE);
            Set<Cookie> cookies = value == null ? Collections.emptySet() : ServerCookieDecoder.LAX.decode(value);
            for (Cookie cookie : cookies) {
                responseContent.append("COOKIE: ").append(cookie.toString()).append("\r\n");
            }
            responseContent.append("\r\n\r\n");

            // query parameters
            QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
            for (Map.Entry<String, List<String>> attr : queryDecoder.parameters().entrySet()) {
                for (String var : attr.getValue()) {
                    responseContent.append("URI: ").append(attr.getKey()).append('=').append(var).append("\r\n");
                }
            }
            responseContent.append("\r\n\r\n");

            // response for get
            if (request.method().equals(HttpMethod.GET)) {
                responseContent.append("\r\n\r\nEND OF GET CONTENT\r\n");
                writeResponse(ctx.channel());
                return;
            }

            // response for post
            if (request.method().equals(HttpMethod.POST)) {
                decoder = new HttpPostRequestDecoder(factory, request);
                readingChunks = HttpUtil.isTransferEncodingChunked(request);

            }
        }
    }

    private void writeResponse(Channel channel) {
    }

    private void writeMenu(ChannelHandlerContext ctx) {
    }
}
