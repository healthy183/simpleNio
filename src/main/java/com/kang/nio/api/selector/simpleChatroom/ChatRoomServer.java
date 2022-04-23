package com.kang.nio.api.selector.simpleChatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @Title 类名
 * @Description 描述
 * @Date 2022/4/16.
 * @Author Administrator
 * @Version
 */
public class ChatRoomServer {

    private Selector selector;
    private ServerSocketChannel ssChannel;
    private  static final int PORT = 9999;

    public  ChatRoomServer(){
        try {
            ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.bind(new InetSocketAddress("127.0.0.1",PORT));
            selector = Selector.open();
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen(){
        System.out.println("监听线程："+Thread.currentThread().getName());
            try {
                while (selector.select()> 0) {
                    System.out.println("开始一轮事件处理");
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()){
                        SelectionKey sk = it.next();
                        if(sk.isAcceptable()){
                            SocketChannel sChannel = ssChannel.accept();
                            sChannel.configureBlocking(false);
                            System.out.println("sChannel远程上线地址："+sChannel.getRemoteAddress());
                            sChannel.register(selector, SelectionKey.OP_READ);
                        }else if(sk.isReadable()){
                            readData(sk);
                        }
                        it.remove();
                    }
                };
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    //读取客户端消息
    private void readData(SelectionKey key) {
        SocketChannel  channel = null;
        try {
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            if(count > 0){
                String msg = new String(buffer.array());
                System.out.println("from  client msg:"+msg);
                //转发消息
                sendInfoToOtherClients(msg, channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + " 离线了..");
                e.printStackTrace();
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            }catch (IOException e2) {
                e2.printStackTrace();;
            }
        }
    }

    private void sendInfoToOtherClients(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中");
        System.out.println("服务器转发数据给客户端线程："+Thread.currentThread().getName());
        for(SelectionKey key : selector.keys()){
            Channel targetChannel = key.channel();
            if(targetChannel instanceof  SocketChannel && targetChannel != self){
                SocketChannel dest = (SocketChannel) targetChannel;
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        ChatRoomServer chatRoomServer = new ChatRoomServer();
        chatRoomServer.listen();
    }
}
