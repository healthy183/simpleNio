package com.kang.nio.net.blockChatroom;

import com.kang.nio.util.NetUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * User:
 * Description:
 * Date: 2022-04-17
 * Time: 19:20
 */
public class ChatroomClient {

    public static void main(String[] args) {

        try(SocketChannel socketChannel = SocketChannel.open()){
            socketChannel.connect
                    (new InetSocketAddress(NetUtils.IP,NetUtils.PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
