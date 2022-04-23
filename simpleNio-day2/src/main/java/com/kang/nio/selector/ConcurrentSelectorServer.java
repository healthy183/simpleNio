package com.kang.nio.selector;

import com.kang.nio.util.ByteBufferUtil;
import com.kang.nio.util.NetUtils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User:
 * Description:
 * Date: 2022-04-17
 * Time: 23:17
 */
public class ConcurrentSelectorServer {

    public static void main(String[] args) {

        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            Thread.currentThread().setName("Boss");
            server.bind(new InetSocketAddress(NetUtils.PORT));
            //负责轮询accept事件的Selector
            Selector boss = Selector.open();
            server.configureBlocking(false);
            server.register(boss, SelectionKey.OP_ACCEPT);
            Worker[] workers = new Worker[4];
            AtomicInteger robin = new AtomicInteger(0);
            for (int i = 0; i <workers.length ; i++) {
                workers[i] = new Worker("worker-" + i);
            }
            while (true){
                boss.select();
                Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isAcceptable()){
                        SocketChannel socket = server.accept();
                        System.out.println("conneting....");
                        socket.configureBlocking(false);
                        System.out.println("before read");
                        //socket注册到Worker的Selector中
                        //负载均衡，轮询分配Worker
                        workers[robin.getAndIncrement()% workers.length].register(socket);
                        System.out.println("read after");
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Worker implements Runnable{

        private Thread thread;
        private volatile Selector selector;
        private String name;
        private volatile boolean started = false;

        private ConcurrentLinkedQueue<Runnable> queue;

        public Worker(String name) {
            this.name = name;
        }

        public void register(final SocketChannel socket) throws IOException {
            //只启动一次
            if(!started){
                thread = new Thread(this,name);
                selector = Selector.open();
                queue = new ConcurrentLinkedQueue<>();
                thread.start();
                started = true;
            }
            queue.add(new Runnable(){
                @Override
                public void run() {
                    try {
                        socket.register(selector,SelectionKey.OP_READ);
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    }
                }
            });
            //唤醒阻塞的Selector
            //select类似LockSupport中的park
            //wakeup的原理类似LockSupport中的unpark
            selector.wakeup();
        }


        @Override
        public void run() {
                while(true){
                    try {
                        selector.select();
                        Runnable task = queue.poll();
                        if(task != null){
                            //获取任务，执行注册操作
                            task.run();
                        }
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        while (iterator.hasNext()){
                            SelectionKey key = iterator.next();
                            if(key.isReadable()){
                                SocketChannel socket = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(16);
                                socket.read(buffer);
                                buffer.flip();
                                ByteBufferUtil.debugRead(buffer);
                            }
                            iterator.remove();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}
