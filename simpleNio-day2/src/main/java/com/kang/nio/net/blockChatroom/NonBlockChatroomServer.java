package com.kang.nio.net.blockChatroom;

import com.kang.nio.util.ByteBufferUtil;
import com.kang.nio.util.NetUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * 一个费非阻塞的服务器，但是这样不停while循环浪费系统哦资源
 * 生产并不会这么干，建议使用Selector
 */
public class NonBlockChatroomServer {

    public static void main(String[] args) throws InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(NetUtils.PORT));
            ArrayList<SocketChannel> channels = new ArrayList<>();
            while (true){
                System.out.println("connect starting");
                //设置非阻塞
                server.configureBlocking(false);
                //无新连接会返回null
                SocketChannel socketChannel = server.accept();
                if(socketChannel != null){
                    System.out.println("connect successfully");
                    channels.add(socketChannel);
                }
                for (SocketChannel channel : channels) {
                    System.out.println("read starting");
                    channel.configureBlocking(false);
                    //通道无数据则返回0
                    int read = channel.read(buffer);
                    if(read > 0){
                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                        buffer.clear();
                        System.out.println("read successfully");
                    }
                }
                try {
                     Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
