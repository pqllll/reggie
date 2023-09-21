package com.controller;

import com.common.R;
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

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basepath;

    /**
     * 目前上传到电脑的D盘，当部署到服务器的时候，可以将basename改为服务器所需要的地址
     * 上传图片
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        String filename = UUID.randomUUID().toString() + file.getOriginalFilename();
        try {
            file.transferTo(new File(basepath, filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(filename);

    }

    @GetMapping("/download")
    public R<String> download(String name, HttpServletResponse reponse) {

        try {
            //输入流
            FileInputStream fileInputStream = new FileInputStream(new File(basepath + name));

            //输出流
            ServletOutputStream OutputStream = reponse.getOutputStream();

            //设置文件格式

            reponse.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {

                OutputStream.write(bytes, 0, len);
                OutputStream.flush();

            }
            OutputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
