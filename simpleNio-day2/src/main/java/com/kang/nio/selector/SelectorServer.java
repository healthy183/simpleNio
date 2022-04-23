package com.kang.nio.selector;

import com.kang.nio.util.ByteBufferUtil;
import com.kang.nio.util.NetUtils;
import com.sun.prism.impl.BufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 通道设置为非阻塞模式，则channel必须在非阻塞模式下工作
 * 绑定时间类型分为：
 * 1，connect 连接时触发
 * 2，accept 服务端接收到数据时触发，有接收能力弱的时候会导致数据暂不能读入
 * 3，read 服务端的数据可写出时触发，有发送(写出)能力弱的时候会导致数据暂不能读入
 *
 * 本服务器端缺点：未解决粘包问题
 */
public class SelectorServer {

    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(16);
        try(ServerSocketChannel server = ServerSocketChannel.open();){
            server.bind(new InetSocketAddress(NetUtils.PORT));
            Selector selector = Selector.open();
            server.configureBlocking(false);
            //server注册selector
            server.register(selector,SelectionKey.OP_ACCEPT);
            while(true){
                //阻塞到绑定事件发生
                int ready = selector.select();//可以设置超时的select(ms)
                //int ready = selector.selectNow();//不阻塞，立即返回
                System.out.println("selector ready counts: "+ready);
                //获取所有事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isAcceptable()){
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        System.out.println("before accepting");
                        /**
                         * 获取连接且必须处理，否则需要取消remove();
                         * 不取消会导致下次依然触发，因为nio底层是水平触发
                         * */
                        SocketChannel socketChannel = channel.accept();
                        System.out.println("after accepting");
                        socketChannel.configureBlocking(false);//非阻塞
                        //socketChannel注册到selector
                        socketChannel.register(selector,SelectionKey.OP_READ);
                        iterator.remove();//取消
                    }else if(key.isReadable()){
                        SocketChannel channel = (SocketChannel) key.channel();
                        System.out.println("before reading");
                        int read = channel.read(buffer);
                        if(read > -1){
                            System.out.println("after reading");
                            buffer.flip();
                            ByteBufferUtil.debugRead(buffer);
                            buffer.clear();
                        }else{//客户端正常断开，返回-1 PS:异常断开会抛出异常
                            key.cancel();
                        }
                        iterator.remove();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
