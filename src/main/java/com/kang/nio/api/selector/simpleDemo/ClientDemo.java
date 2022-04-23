package com.kang.nio.api.selector.simpleDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Title 类名
 * @Description 描述
 * @Date 2022/4/16.
 * @Author Administrator
 * @Version
 */
public class ClientDemo {

    public static void main(String[] args) {
        // 获取通道
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));
            // 切换非阻塞
            socketChannel.configureBlocking(false);
            //分配缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);
            //发送数据
            Scanner scan = new Scanner(System.in);
            System.out.print("请输入:");
            while(scan.hasNext()){
                String str = scan.nextLine();
                buf.put((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(System.currentTimeMillis()) + "\n" + str).getBytes());
                buf.flip();
                socketChannel.write(buf);
                buf.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(socketChannel != null){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
