package com.example.reggie.controller;

import com.example.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
/**
 * 处理文件的上传、下载
 */
public class CommonController {
    @Value("${file.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("客户端上传文件");
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //创建一个目录对象（目录也可以看成是一个文件）
        File dir = new File(uploadPath);
        //判断目录是否存在，不存在我们需要创建
        if(!dir.exists()) {
            dir.mkdirs();
        }

        //使用UUID重新生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        try {
            //将文件临时转存到指定位置
            file.transferTo(new File(uploadPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //需要返回文件名称给页面，页面提交到菜品的数据库中
        return Result.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //获取输入流，从服务器读数据
            FileInputStream inputStream = new FileInputStream(uploadPath + name);

            //通过response对象，获取到输出流
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应数据格式为image/jpeg
            response.setContentType("image/jpeg");

            byte[] bytes = new byte[1024];
            int len = 0;
            while((len = (inputStream.read(bytes))) != -1 ) {
                outputStream.write(bytes, 0, len);
                //刷新缓存
                outputStream.flush();
            }

            //记得关闭资源
            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
