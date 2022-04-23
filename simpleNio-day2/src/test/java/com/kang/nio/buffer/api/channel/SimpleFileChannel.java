package com.kang.nio.buffer.api.channel;

import com.kang.nio.util.ByteBufferUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * FileChannel 是阻塞，所有无法搭建Selector
 * FileChannel无法直接创建，只可以通过以下三种方式创建
 * FileInputStream ：获取只读的FileChannel
 * FileOutputStream ：获取只写的FileChannel
 * RandomAccessFile  ：能否读写视RandomAccessFile当前的读写模式
 */
public class SimpleFileChannel {

    @Test
    public void write() {
        String fileName = "fileChannel.txt";
        new File(fileName).delete();
        try (FileOutputStream fileInputStream = new FileOutputStream(fileName);
             FileChannel channel = fileInputStream.getChannel();) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(32);
            byteBuffer.put("FileChannel".getBytes());
            byteBuffer.flip();
            //判断缓冲区是否还有数据未写入channel
            while (byteBuffer.hasRemaining()){
                int write = channel.write(byteBuffer);
                System.out.println(write);
            }
            long position = channel.position();
            System.out.println("position:"+position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read(){
        try (FileInputStream fileInputStream = new FileInputStream("note.txt");
             FileChannel channel = fileInputStream.getChannel();) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(64);
            int read = channel.read(byteBuffer);//读书长度
            System.out.println("read:"+read);
            long position = channel.position();
            System.out.println("position:"+position);//读后所在指针
            ByteBufferUtil.debugAll(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 只能传输2G内文件的问题
     * 实现0拷贝，效率高
     */
    @Test
    public void  transferTo(){
        try (FileInputStream fileInputStream = new FileInputStream("note.txt");
             FileOutputStream fileOutputStream = new FileOutputStream("noteCope.txt");
             FileChannel inputFileChannel = fileInputStream.getChannel();
             FileChannel outputChannel = fileOutputStream.getChannel()
             ) {
            inputFileChannel.transferTo(0,inputFileChannel.size(),outputChannel);
            System.out.println("transferTo 成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 只能传输2G内文件需要迭代多次操作
     *
     */
    @Test
    public void  transferToOverSize(){
        try (FileInputStream fileInputStream = new FileInputStream("note.txt");
             FileOutputStream fileOutputStream = new FileOutputStream("noteCopeOverSize.txt");
             FileChannel inputFileChannel = fileInputStream.getChannel();
             FileChannel outputChannel = fileOutputStream.getChannel()
        ) {
            long size = inputFileChannel.size();
            long capacity = inputFileChannel.size();//总长度
            while(capacity > 0){
                long position = size-capacity;//指针
                // transferTo返回值为传输了的字节数
                long readedCount = inputFileChannel.transferTo(position, capacity, outputChannel);
                //capacity -=  readedCount;
                capacity = capacity - readedCount;
                //capacity = capacity - inputFileChannel.transferTo(position, capacity,outputChannel);
            }
            System.out.println("transferTo 成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void simpleRun(){
        int i  = 10 ;
        int j = 20;
        i -= j;
        System.out.println(i);//-10
    }

}
