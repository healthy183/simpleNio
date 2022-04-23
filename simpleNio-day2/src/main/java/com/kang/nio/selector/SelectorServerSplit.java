package com.kang.nio.selector;

import com.kang.nio.util.ByteBufferUtil;
import com.kang.nio.util.NetUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 按分隔符处理长消息
 */
public class SelectorServerSplit {

    public static void main(String[] args) {

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
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        /**
                         *1，socketChannel注册到selector
                         *2，添加专有通道对应的Buffer附件
                         *PS:register(Selector, int SelectionKey,Object)
                         *最后一个参数是Object，作为channel附件，可以自定义字段
                         * 获取附件 SelectionKey#attachment()
                        */
                        socketChannel.register(selector,SelectionKey.OP_READ,buffer);
                        iterator.remove();
                    }else if(key.isReadable()){
                        SocketChannel channel = (SocketChannel) key.channel();
                        System.out.println("before reading");
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        if(read > -1){
                            //读数据，如果有粘包问题则使用分隔符分割buffer中数据
                            split(buffer);
                            //有粘包则扩容一倍
                            if(buffer.position() == buffer.limit()){
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.position() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);//替换附件
                            }
                        }else{
                            key.cancel();
                            channel.close();
                        }
                        System.out.println("after reading");
                        iterator.remove();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for(int i = 0; i < buffer.limit(); i++) {
            // 遍历寻找分隔符
            // get(i)不会移动position
            if (buffer.get(i) == '\n') {
                // 缓冲区长度
                int length = i+1-buffer.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                // 将前面的内容写入target缓冲区
                for(int j = 0; j < length; j++) {
                    // 将buffer中的数据写入target中
                    target.put(buffer.get());
                }
                // 打印结果
                ByteBufferUtil.debugAll(target);
            }
        }
        // 切换为写模式，但是缓冲区可能未读完，这里需要使用compact
        buffer.compact();
    }
}
