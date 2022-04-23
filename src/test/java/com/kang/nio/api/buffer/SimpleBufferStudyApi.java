package com.kang.nio.api.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @Title 类名
 * @Description 描述
 * @Date 2022/4/11.
 * @Author Administrator
 * @Version
 */
public class SimpleBufferStudyApi {

    @Test
    public void  allocate(){
        /**
         * allocate实际是java.nio.HeapByteBuffer 非直接内存(堆内存)
         * 从本进程内存复制到直接内存，再利用本地io处理
         * 本地IO-->直接内存-->非直接内存-->直接内存-->本地IO
         */
        ByteBuffer allocate = ByteBuffer.allocate(10);//非直接内存(堆内存)
        System.out.println(allocate.isDirect());
        /*
        * allocateDirect实际是java.nio.DirectByteBuffer
        * 本地IO-->直接内存-->本地IO
        * 在做IO处理时，比如网络发送大量数据时，直接内存会具有更高的效率。
        * 直接内存使用allocateDirect创建，但是它比申请普通的堆内存需要耗费更高的性能。
        */
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(20);//直接内存、非堆内存
        System.out.println(allocateDirect.isDirect());
    }

    /**
     * 学习api
     * allocate() 创建堆内存buffer
     * put() 保存数据
     * flip()、 切换读模式
     * get()、读数据,读完后已读指针+1
     * get(i)、读数据,读完后已读指针不变
     * rewind()、重复读数据
     * clear()  切换写模式，并“遗忘”旧数据
     */
    @Test
    public void studyApi(){
        String str = "itHealthy";
        //创建
        ByteBuffer buf = ByteBuffer.allocate(10);
        System.out.println("----allocate()-----");
        printlnApi(buf);
        //put
        buf.put(str.getBytes());
        System.out.println("----put()-----");
        printlnApi(buf);
        //flip切换读模式
        buf.flip();
        System.out.println("----flip()-----");
        printlnApi(buf);
        //get() 读数
        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        System.out.println(new String(dst, 0, dst.length));
        System.out.println("----get()-----");
        printlnApi(buf);
        //rewind() 可重复读
        buf.rewind();
        System.out.println("----rewind()-----");
        printlnApi(buf);
        //clear() 清空缓冲区 --实际是切回去写模式，数据还在，只是被“遗忘”状态
        buf.clear();
        System.out.println("----clear()-----");
        printlnApi(buf);
    }

    /**
     * 学习api
     * mark() 标记已读指针
     * reset() 滚到上次标记已读指针
     * hasRemaining() 判断buffer是否有剩余数据未读
     * remaining() buffer可操作数据的数据量
     */
    @Test
    public void studyApi2(){
        String str = "itHealthy";
        //创建
        ByteBuffer buf = ByteBuffer.allocate(1024);
        //put
        buf.put(str.getBytes());
        //flip切换读模式
        buf.flip();
        byte[] dst = new byte[buf.limit()];
        buf.get(dst,0,2);
        System.out.println(new String(dst, 0, dst.length));
        System.out.println(buf.position());//指针为2
        //mark 标记已读指针
        buf.mark();
        buf.get(dst,2,2);//再读两个指针数据
        System.out.println(new String(dst, 2, 2));
        System.out.println(buf.position());//指针为4
        //reset 回滚到上次标记已读指针
        buf.reset();
        System.out.println(buf.position());//指针为2
        //判断buffer是否有剩余数据未读
        if(buf.hasRemaining()){
            //buffer可操作数据的数据量
            System.out.println(buf.remaining());
        }
    }

    /**
     * @param buf
     * 打印buf的属性
     */
    private void printlnApi(ByteBuffer buf) {
        ByteBufferUtil.debugAll(buf);
       /* System.out.println(buf.position());  //当前待操纵下标
        System.out.println(buf.limit()); //buffer可读界限
        System.out.println(buf.capacity()); //buffer总长度*/
    }

}
