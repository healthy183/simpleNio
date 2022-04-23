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
 * User:
 * Description:
 * Date: 2022-04-17
 * Time: 18:27
 */
public class BlockChatroomServer {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(NetUtils.PORT));
            ArrayList<SocketChannel> channels = new ArrayList<>();
            while (true){
                System.out.println("connect starting");
                //无新连接会阻塞，并影响读取旧连接数据
                SocketChannel socketChannel = server.accept();
                System.out.println("connect successfully");
                channels.add(socketChannel);
                for (SocketChannel channel: channels) {
                    System.out.println("read starting");
                    //通道无数据会阻塞
                    channel.read(buffer);
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    System.out.println("read successfully");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
