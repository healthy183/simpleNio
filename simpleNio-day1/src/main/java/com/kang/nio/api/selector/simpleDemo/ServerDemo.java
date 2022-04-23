package com.kang.nio.api.selector.simpleDemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @Title 类名
 * @Description 描述
 * @Date 2022/4/16.
 * @Author Administrator
 * @Version
 */
public class ServerDemo {

    public static void main(String[] args) {
        try {
            //1 获取通道
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            //2切换非阻塞模式
            ssChannel.configureBlocking(false);
            //3 绑定端口
            ssChannel.bind(new InetSocketAddress(9999));
            //4 获取选择器
            Selector selector = Selector.open();
            //5 通道注册到选择器，并指定“监听接收事件”
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);
            //6 轮询获取选择器已经“准备就绪”事件
            System.out.println("服务器启动成功，并开始轮询");
            while(selector.select() > 0){
                System.out.println("收到select加入");
                // 7 获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()){
                    //8 获取准备就绪的事件
                    SelectionKey sk = it.next();
                    //9 判断具体是什么事件准备就绪
                    if(sk.isAcceptable()){
                        //10  若“接收就绪”获取客户端连接
                        SocketChannel scChannel = ssChannel.accept();
                        //11切换非阻塞模式
                        scChannel.configureBlocking(false);
                        //12 通道注册到选择器上
                        scChannel.register(selector,SelectionKey.OP_READ);
                    }else if(sk.isReadable()){
                        //13 获取选择器“读就绪”状态的通道
                        SocketChannel scChannel = (SocketChannel) sk.channel();
                        //14 读取数据
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        int len = 0;
                        while((len = scChannel.read(buf)) > 0){
                            buf.flip();
                            System.out.println(new String(buf.array(),0,len));
                            buf.clear();
                        }
                    }
                    //15 取消选择器  SelectionKey
                    it.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
