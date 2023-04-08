package com.zjj.reggie.controller;

import com.zjj.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @create: 2023-04-05 14:09
 * @author: Junj_Zou
 * @Description:
 */

/**
 * 通过控制器，主要用于 文件上传 文件下载
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basepath;

    /**
     * 文件上传 并且通过uuid防止文件重名
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestPart("file") MultipartFile file) throws IOException {
        log.info("上传文件为: {}", file.getOriginalFilename());

        File dir = new File(basepath);
        // 判断一下当前路径是否存在，不存在则创建
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 获取上传文件原名
        String originalFilename = file.getOriginalFilename();
        // 获取后缀名
        String hzName = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 使用UUID解决文件重名问题
        String fileName = UUID.randomUUID() + hzName;

        try {
            file.transferTo(new File(basepath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }

        // 将文件名返回给前端，便于后期的开发
        return Result.success(fileName);
    }

    /**
     * 文件下载
     * 其实就是将 文件 从 服务器 复制到 浏览器
     * @param name
     * @return
     */
    @GetMapping("/download")
    public void download(@RequestParam("name") String name, HttpServletResponse response) {
        FileInputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            // 输入流: 从服务器端读数据
            inputStream = new FileInputStream(new File(basepath + name));

            // 输出流: 向浏览器写数据
            outputStream = response.getOutputStream();

            // 定义缓冲区
            int len;
            byte[] buffer = new byte[1024];

            // 复制
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                // 关闭输入输出流
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
