package com.kang.nio.buffer.api.bio;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User:
 * Description:
 * Date: 2022-04-17
 * Time: 13:23
 */
public class SimpleFile {

    @Test
    public void exists(){
        Path path = Paths.get("fileChannel.txt");
        System.out.println(path);
        System.out.println(Files.exists(path));
    }

    /**
     * 新建文件夹
     */
    @Test
    public void createDirectory(){
        try {
            Path path = Paths.get("D:\\code\\efg");
            Files.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新建多个文件夹
     */
    @Test
    public void createDirectories(){
        try {
            Path path = Paths.get("D:\\aaee\\efg\\abc");
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void cope(){
        try {
            Path source = Paths.get("fileChannel.txt");
            Path copy= Paths.get("fileChannelCopy.txt");
            Files.copy(source,copy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 替换文件
     * source替换copy，并删除source
     * StandardCopyOption.ATOMIC_MOVE 保证文件移动的原子性
     */
    @Test
    public void move(){
        try {
            Path source = Paths.get("fileChannel.txt");
            Path copy = Paths.get("fileChannelCopy.txt");
            Files.move(source,copy,StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delete() throws IOException {
        Path copy = Paths.get("fileChannelCopy.txt");
        Files.delete(copy);//也可以用于删除空文件夹
    }

    @Test
    public void SimpleFileVisitor() throws IOException {
        Path path = Paths.get("D:\\datas");
        // 文件目录数目
        AtomicInteger dirCount = new AtomicInteger();
        // 文件数目
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("===>"+dir);
                // 增加文件目录数
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                // 增加文件数
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        // 打印数目
        System.out.println("文件目录数:"+dirCount.get());
        System.out.println("文件数:"+fileCount.get());
    }

}
