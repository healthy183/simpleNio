package com.kang.nio.buffer.api.bio;

import org.junit.Test;

import java.nio.file.Paths;

/**
 * User:
 * Description:
 * Date: 2022-04-17
 * Time: 13:16
 */
public class SimplePath {

    @Test
    public void get(){

        java.nio.file.Path path = Paths.get("1.txt");
        System.out.println(path.toString());

        java.nio.file.Path notePath = Paths.get("D:\\code\\note.txt");
        System.out.println(notePath.toString());

        java.nio.file.Path notePath2 = Paths.get("D:/code/note.txt");
        System.out.println(notePath2.toString());

        java.nio.file.Path cvPath = Paths.get("D:\\code\\..\\cv");
        System.out.println(cvPath.toString());//..表示上一级
        System.out.println(cvPath.normalize());//校正后路径

    }


}
