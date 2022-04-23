package com.kang.nio.api.channel;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Title 类名
 * @Description 描述
 * @Date 2022/4/14.
 * @Author Administrator
 * @Version
 */
public class SimpleChannelStudyApi {

    /**
     * 写文件
     */
    @Test
    public void write(){
        try (FileOutputStream fos = new FileOutputStream("data01.txt");
             FileChannel channel = fos.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("itHealthy".getBytes());
            buffer.flip();
            channel.write(buffer);
            System.out.println("写完了");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  read() 读文件
     *  假设文件小于1024字节
     */
    @Test
    public void read(){
        try (FileInputStream is = new FileInputStream("data01.txt");
             FileChannel channel = is.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            String rs = new String(buffer.array(), 0, buffer.remaining());
            System.out.println(rs);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  read() write()实现文件复制
     */
    @Test
    public void copy(){
        String ulr = "D:\\bin";
        String fromFileName = "a.txt";
        File fromFile = new File(ulr + File.separator + fromFileName);
        String toFileName = "aCopy.txt";
        File toFile = new File(ulr + File.separator + toFileName);

        try (FileInputStream fis = new FileInputStream(fromFile);
             FileOutputStream fos = new FileOutputStream(toFile);
        ) {
            FileChannel isChannel = fis.getChannel();
            FileChannel osChannel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while(true){
                buffer.clear();//清空(遗忘)缓冲区
                int flag = isChannel.read(buffer);//每次读1024字节，返回读完后指针
                if(flag == -1){
                    break;
                }
                buffer.flip();
                osChannel.write(buffer);
            }
            System.out.println("copy finish");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 分散和聚集
     * 分散:channel读到多个buffer中
     * 聚集:多个buffer通过channel写到文件
     */
    @Test
    public void   RandomAccessFile(){
        try {
            RandomAccessFile raf
                    = new RandomAccessFile("data01.txt", "rw");
            try(FileChannel channel = raf.getChannel()){
                ByteBuffer bufTo = ByteBuffer.allocate(100);
                ByteBuffer bufFrom = ByteBuffer.allocate(1024);
                //分散读取
                ByteBuffer[] bufs = {bufTo,bufFrom};
                channel.read(bufs);

                for(ByteBuffer buf : bufs){
                    buf.flip();//读
                }
                System.out.println(new String(bufTo.array(),0,bufTo.limit()));
                System.out.println(new String(bufFrom.array(),0,bufFrom.limit()));
                //聚集写入
                RandomAccessFile raf2
                        = new RandomAccessFile("data02.txt", "rw");
                try (FileChannel raf2Channel = raf2.getChannel()) {
                    raf2Channel.write(bufs);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转换文件
     */
    @Test
    public void transferFrom(){
        try (FileInputStream is = new FileInputStream("data01.txt");
             FileOutputStream os = new FileOutputStream("data04.txt");
             FileChannel isChannel = is.getChannel();
             FileChannel osChannel = os.getChannel()) {
            osChannel.transferFrom(isChannel,isChannel.position(),isChannel.size());

            isChannel.transferTo(osChannel.position(),osChannel.size(),osChannel);
        } catch (IOException ioException) {
        }
    }

    /**
     * 转换文件
     */
    @Test
    public void transferTo(){
        try (FileInputStream is = new FileInputStream("data01.txt");
             FileOutputStream os = new FileOutputStream("data05.txt");
             FileChannel isChannel = is.getChannel();
             FileChannel osChannel = os.getChannel()) {
            isChannel.transferTo(isChannel.position(),isChannel.size(),osChannel);
        } catch (IOException ioException) {
        }
    }

}
