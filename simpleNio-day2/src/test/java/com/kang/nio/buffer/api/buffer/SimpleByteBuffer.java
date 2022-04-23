package com.kang.nio.buffer.api.buffer;

import com.kang.nio.util.ByteBufferUtil;
import org.junit.Test;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * User:
 * Description:
 * Date: 2022-04-16
 * Time: 20:23
 */
public class SimpleByteBuffer {

    @Test
    public void api(){
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte)97); //put a进去
        ByteBufferUtil.debugAll(buffer);

        buffer.put(new byte[]{98,99,100,101});//put b c d e
        ByteBufferUtil.debugAll(buffer);
        //切换模式  默认写，所以现在切成读；读完再切回去写的limit跟之前读模式的一样
        buffer.flip();
        ByteBufferUtil.debugAll(buffer);
        //获取字段  position指针+1
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);
        //再拿一次 position指针再+1
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);
        //buffer.get(i)后，position指针不变
        System.out.println(buffer.get(0));
        ByteBufferUtil.debugAll(buffer);
        //使用compact切换至写模式
        //本来是abcde 已经读了a、b, 将未读的压缩出去
        //终变成cdede
        buffer.compact();
        ByteBufferUtil.debugAll(buffer); //position: [3], limit: [10] capacity: [10]
        buffer.put((byte)102); //put f进去
        buffer.put((byte)103); //put g进去
        //有趣的是原本cdede，de被替换成cdefg
        ByteBufferUtil.debugAll(buffer); //position: [5], limit: [10] capacity: [10]
        //遗忘数据，“重置”buffer，并开始写模式，重复写完则覆盖原来数据
        buffer.clear(); //position: [0], limit: [10] capacity: [10]
        ByteBufferUtil.debugAll(buffer);
    }

    /**
     * 测试flip()切换到读模式后直接写数据的情况
     * 结论：可以写入，但是position+1,但是再读则BufferUnderflowException
     */
    @Test
    public void flip(){
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte)97); //put a进去
        ByteBufferUtil.debugAll(buffer);
        //输出0,读不到东西,但是position+1
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);
        //切换到读模式
        buffer.flip(); //由于上面position+1，所以limit+1
        ByteBufferUtil.debugAll(buffer);
        //输出97
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);
        //不切换直接写，可以写入，同时position+1
        buffer.put((byte)98); //put b进去
        ByteBufferUtil.debugAll(buffer);
        /**
         *由于上面的position+1，这里get()就抛异常：BufferUnderflowException
         * */
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);
    }

    /**
     * 测试flip()切换到读模式后直接写数据的情况
     * 结论：但是再写则 BufferOverflowException
     */
    @Test
    public void flip2(){
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte)97); //put a进去
        ByteBufferUtil.debugAll(buffer);
        //输出0,读不到东西,但是position+1
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);
        //切换到读模式
        buffer.flip(); //由于上面position+1，所以limit+1
        ByteBufferUtil.debugAll(buffer);
        //输出97
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);
        //不切换直接写，可以写入，同时position+1
        buffer.put((byte)98); //put b进去
        ByteBufferUtil.debugAll(buffer);
        /**
         *由于上面的position+1，这里put()就抛异常：BufferOverflowException
         * */
        buffer.put((byte)99); //put c进去
        ByteBufferUtil.debugAll(buffer);

    }

    @Test
    public void flip3(){

        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte)97); //put a进去
        ByteBufferUtil.debugAll(buffer);
        //切换到读模式
        buffer.flip();
        //输出97
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);

        //切换到写模式,position为0，但是limit不变还是1
        buffer.flip();
        ByteBufferUtil.debugAll(buffer);

        buffer.put((byte)98); //put b进去
        ByteBufferUtil.debugAll(buffer);

    }

    @Test
    public void flip4(){
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{97,98,99,100,101});//put a b c d e
        buffer.flip(); //切换读模式
        buffer.get();
        buffer.get();
        buffer.flip(); //切换写模式 limit为2
        ByteBufferUtil.debugAll(buffer);
    }


    /**
     * 除了flip可以切换写模式，compact 、clear都是切换
     * flip()切换写模式后limit不变
     * clear() '遗忘'以往数据，重置position、limit、mark，重新写入则覆盖
     * compact() ByteBuffer的方法，跟Buffer没有关系
     *  将已读数据切走，并由未读数据向前填充,设计到数组拷贝，所以更加耗性能
     */
    @Test
    public void compactAndClear(){
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{97,98,99,100,101});//put a b c d e
        buffer.flip(); //切换读模式
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);//position: [2], limit: [5] capacity: [10]

        buffer.compact(); //position: [3], limit: [10] capacity: [10]
        ByteBufferUtil.debugAll(buffer); //由"abcde"变成”cdede“

        buffer.clear(); //position: [0], limit: [10] capacity: [10]
        ByteBufferUtil.debugAll(buffer);
    }
}
