package com.james.test;

import org.junit.jupiter.api.Test;

public class UploadFileTest {

    @Test
    public void test1(){
        String fileName = "edqweqw.jpg";

        String suffix = fileName.substring(fileName.lastIndexOf("."));

        System.out.println(suffix);
    }
}
