package com.bysj_backend.controller;

import com.bysj_backend.utils.OperationResult;
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
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //读取配置文件中的配置 通过value取出来
    @Value("${bysj.path}")
    private String basePath;


    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public OperationResult<String> upload(MultipartFile file) {
        OperationResult<String> operationResult = new OperationResult<>();
        //file是一个临时文件,需要转存到指定位置,否则本次请求完成后临时文件会被删除
        log.info(file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取文件的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用uuid重新生成文件名,防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString().replaceAll("-","") + suffix;
        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()) {
            //目录不存在需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        operationResult.setData(fileName);
        operationResult.setMessage("文件名 : " + fileName);
        operationResult.setStatus(OperationResult.STATUS_SUCCESS);

        return operationResult;
    }


    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        //输入流,读取文件内容
        try {
            fileInputStream = new FileInputStream(new File(basePath + name));
            log.info("文件路劲:  "  + basePath + name);
            //输出流,将文件写回浏览器,在浏览器展示图片
            outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            response.setContentType("image/png");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                outputStream.close();
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}


