package com.kang.nio.selector;

import com.kang.nio.util.NetUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * User:
 * Description:
 * Date: 2022-04-17
 * Time: 22:45
 */
public class SelectorServerWrite {

    public static void main(String[] args) {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(NetUtils.PORT));
            server.configureBlocking(false);
            Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            while(true){
                //阻塞到绑定事件发生
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isAcceptable()){
                        SocketChannel socket = server.accept();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < 500000000; i++) {
                            stringBuilder.append("a");
                        }
                        ByteBuffer buffer = StandardCharsets.UTF_8.encode(stringBuilder.toString());
                        //再执行一次Buffer->Channel的写入，如果未写完，则添加一个写入时间
                        int write = socket.write(buffer);
                        System.out.println("write count:"+write);
                        //通道可能无法放入缓存区中所有数据
                        if(buffer.hasRemaining()){
                            //注册到Selector中，关注可写事件，并将buffer作为附件
                            socket.configureBlocking(false);
                            socket.register(selector,SelectionKey.OP_WRITE, buffer);
                        }
                    }else if(key.isWritable()){
                        SocketChannel socket = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int write = socket.write(buffer);
                        System.out.println(write);
                        //如果已经完成写操作，需要移除key中附件，并且不对读事件甘兴趣
                        if(!buffer.hasRemaining()){
                            key.attach(null);
                            key.interestOps(0);
                        }


                    }


                    iterator.remove();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
