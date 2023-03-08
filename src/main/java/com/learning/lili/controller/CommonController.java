package com.learning.lili.controller;

import com.learning.lili.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${lili.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file 和前端控件name一致
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // 当前file是临时文件，需要转存到指定位置，否则请求结束后，该文件即被删除
        log.info("file：{}", file.toString());

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));

        // 使用UUID重新生成文件名，防止文件名重复导致文件覆盖
        String filename = UUID.randomUUID().toString() + suffix;

        // 判断当前目录是否存在
        File dir = new File(basePath);
        if (!dir.exists()) {
            // 如果不存在，先进行创建
            dir.mkdir();
        }

        try {
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(filename);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        log.info("文件下载name：{}", name);
        try {
            // 输入流：通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            // 输出流：通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            // 设置响应数据MIME类型
            response.setContentType("image/jpeg");

            // 通过字节数组的形式输出
            int len = 0;
            byte[] bytes = new byte[1024];
            while ( (len = fileInputStream.read(bytes)) != -1 ) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            // 关闭资源
            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
