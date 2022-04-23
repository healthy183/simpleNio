package com.kang.nio.buffer.api.buffer;
/**
 * 粘包
 * 发送端大量消息发送的时候，往往是多条合并发，但是缓冲区容量有效，
 * 最终导致类似是N+0.5条信息发送出去，剩余0.5条下次发送。
 * 期望：
 * i am healthy. \n
 * how are you? \n
 * why are you late? \n
 *
 * 实际：
 * i am healthy. \nhow are
 * you? \nwhy are you late? \n
 *
 * 这种情况就是粘包
 */
/**
 * 半包
 * 同样接收端设置的缓冲区也有限，也会导致多条信息合并接受，
 * 也会出现发送端那样的粘包情况，接收端叫“半包”
 */
import com.kang.nio.util.ByteBufferUtil;
import org.junit.Test;
import java.nio.ByteBuffer;

/**
 * 本demo用户解决粘包问题
 */
 public class SimpleStickyPackage {

     @Test
     public void compact(){
         /*String hello = "i am healthy. \\n";
         String howOne = "how ar";
         String howTwo =  "e you? \\n";*/
         //String why = "why are you late? \\n";
         java.lang.String hello = "Hello,world\n";
         java.lang.String howOne = "I'm Nyima\nHo";
         java.lang.String howTwo = "w are you?\n";

         ByteBuffer byteBuffer = ByteBuffer.allocate(32);
         byteBuffer.put((hello+howOne).getBytes());
         split(byteBuffer);
         byteBuffer.put((howTwo).getBytes());
         /*System.out.println("=======加上新字符串=======");
         ByteBufferUtil.debugAll(byteBuffer);*/
         split(byteBuffer);

     }

    private void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            /*byte[] aa = {1,2,3};
            byte[]  cc =  new  byte[]{1,2,3};
            byte[] bb = new byte[]{buffer.get(i)};
            java.lang.String str = new java.lang.String(bb);*/
            if(buffer.get(i) == '\n'){
                //缓冲区长度
                int length =  i+1-buffer.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j <length ; j++) {
                    target.put(buffer.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        //切换写模式，但是又要保留未读数据
        buffer.compact();
        /*System.out.println("=======切换写模式=======");
        ByteBufferUtil.debugAll(buffer);*/
    }
}
