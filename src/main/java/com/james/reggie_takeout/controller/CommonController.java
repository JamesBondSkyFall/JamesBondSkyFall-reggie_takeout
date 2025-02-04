package com.james.reggie_takeout.controller;

import com.james.reggie_takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        // file 是一个临时文件，需要转存到指定位置，否则本次请求完成后文件会自动删除
        log.info(file.toString());

        // 获取上传文件的原始文件名
        String originalFilename = file.getOriginalFilename();
        // 通过截取最后一个 . 的形式来获得文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 为了防止文件重名而造成文件覆盖，使用 UUID 重新生成文件名
        String randomName = UUID.randomUUID().toString() + suffix;

        File dir = new File(basePath);
        // 判断当前目录是否存在，如果不存在，则需要创建
        if(!dir.exists()){
            dir.mkdirs();
        }

        try {
            // 将临时文件转存到指定位置
            file.transferTo(new File(basePath + randomName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return R.success(randomName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){


        // 输入流，通过输入流读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            // 输出流，通过输出流将文件写回浏览器，在浏览器展示图片

            ServletOutputStream outputStream = response.getOutputStream();

            // response.setContentType("image");
            // outputStream.write();

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes))!= -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            // 关闭资源

            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
