package org.myan.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by myan on 2017/10/25.
 * Intellij IDEA
 */
public class NioTimeServer implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    public NioTimeServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port), 1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new NioTimeServer(8000)).start();
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(1000);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    handleKey(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop () {
        this.stop = true;
    }

    private void handleKey(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                // connect things
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                serverSocketChannel.register(selector, SelectionKey.OP_READ);
            } else if (key.isReadable()) {
                // read from client
                SocketChannel channel = (SocketChannel) key.channel();
                ByteBuffer buff = ByteBuffer.allocate(1024);
                int readBytes = channel.read(buff);
                if (readBytes > 0) {
                    buff.flip();
                    byte[] bytes = new byte[buff.remaining()];
                    String body = new String(bytes, "UTF-8");
                    String result = body.equalsIgnoreCase("QUERY TIME") ? new Date(System.currentTimeMillis()).toString() :
                            "INVALID QUERY";
                    write(channel, result);
                } else if (readBytes < 0) {
                    key.cancel();
                    channel.close();
                }
            }
        }

    }

    private void write(SocketChannel channel, String result) throws IOException {
        if(result != null && result.trim().length() > 0) {
            byte[] bytes = result.getBytes();
            ByteBuffer buff = ByteBuffer.allocate(bytes.length);
            buff.put(bytes);
            buff.flip();
            channel.write(buff);
        }
    }
}
