package com.kang.nio.buffer.api.buffer;

import com.kang.nio.util.ByteBufferUtil;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * User:
 * Description:
 * Date: 2022-04-16
 * Time: 23:02
 */
public class SimpleBufferStringConvent {

    /**
     * ByteBuffer与String的数据转换
     * new ByteBuffer().put(byte[])将String存入ByteBuffer
     */
    @Test
    public void convent(){
        String from = "abc";
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(from.getBytes());
        ByteBufferUtil.debugAll(buffer);

        buffer.flip();
        String to = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(to);
        ByteBufferUtil.debugAll(buffer);
    }

    /**
     * ByteBuffer与String的数据转换
     * StandardCharsets.UTF_8.encode()将String转成ByteBuffer
     */
    @Test
    public void convent2(){
        String from = "abc";
        //encode后ByteBuffer已经是读模式，无需flip()切换
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(from);
        ByteBufferUtil.debugAll(buffer);
        //decode成CharBuffer然后toString()
        String to = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(to);
        ByteBufferUtil.debugAll(buffer);
    }

    /**
     * ByteBuffer.wrap()将String转成ByteBuffer
     */
    @Test
    public void conventWrap(){
        String from = "abc";
        ByteBuffer buffer = ByteBuffer.wrap(from.getBytes());
        ByteBufferUtil.debugAll(buffer);

        String to = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(to);
        ByteBufferUtil.debugAll(buffer);
    }

}
