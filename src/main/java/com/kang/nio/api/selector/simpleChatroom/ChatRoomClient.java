package com.kang.nio.api.selector.simpleChatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Title 类名
 * @Description 描述
 * @Date 2022/4/16.
 * @Author Administrator
 * @Version
 */
public class ChatRoomClient {

    //定义相关的属性
    private final String HOST = "127.0.0.1"; // 服务器的ip
    private final int PORT = 9999; //服务器端口
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    public ChatRoomClient(){
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(userName+" has login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendInfo(String info){
            info = userName + " say: "+ info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void readInfo(){
        try {
            int readChannels = selector.select();
            //有可用通道
            if(readChannels > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                       SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        sc.read(buffer);
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }
                }
                iterator.remove();
            }else{

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatRoomClient chatRoomClient = new ChatRoomClient();
        //每三秒读一次信息
        new Thread(){
            @Override
            public void run() {
                while(true){
                    chatRoomClient.readInfo();
                    try {
                        Thread thread = Thread.currentThread();
                        thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        System.out.print("请说:");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String str = scanner.nextLine();
            chatRoomClient.sendInfo(str);
        }
    }

}
