package com.example.vwalkblog.controller;

import com.example.vwalkblog.respR.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@ResponseBody
@RequestMapping("/image")
@CrossOrigin(origins = "*",maxAge = 3600)
@Slf4j
public class imageController {

    @Value("${vwalkblog.cover-path}")
    private String coverPath;
    @Value("${vwalkblog.image-path}")
    private String imagePath;

    /*
    *   封面上传
    * */
    @PostMapping("/coverUpload")
    public Result<String> upload(@RequestBody MultipartFile cover, HttpServletRequest request){
        // 此时的file是临时文件，需要进行转存
        log.info(cover.toString());
        // 原始文件名
        String originalFilename = cover.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用UUID重新生成文件名，防止文件名称重复
        StringBuffer fileName = new StringBuffer(UUID.randomUUID().toString());
        fileName.append(suffix);
        File dir = new File(coverPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            // 将临时文件存到指定位置
            cover.transferTo(new File(coverPath +fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/image/coverDownload/"+fileName;
        return Result.success(url);
    }

    /*
     *   封面加载
     * */
    @GetMapping("/coverDownload/{name}")
    public void download(@PathVariable String name, HttpServletResponse response) {
        try {
            FileInputStream fileInputStream = new FileInputStream(coverPath + name);
            response.setContentType("image/jpeg");
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copy(fileInputStream,outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /*
     *   正文轮播图片上传
     * */
    @PostMapping("/imageUpload")
    public Result<String> imageUpload(@RequestBody MultipartFile image, HttpServletRequest request){
        // 此时的file是临时文件，需要进行转存
//        log.info(cover.toString());
        // 原始文件名
        String originalFilename = image.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用UUID重新生成文件名，防止文件名称重复
        StringBuffer fileName = new StringBuffer(UUID.randomUUID().toString());
        fileName.append(suffix);
        File dir = new File(imagePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            // 将临时文件存到指定位置
            image.transferTo(new File(imagePath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/image/imageDownload/"+fileName;
        return Result.success(url);
    }

    /*
     *   正文轮播图片加载
     * */
    @GetMapping("/imageDownload/{name}")
    public void imageDownload(@PathVariable String name, HttpServletResponse response) {
        try {
            FileInputStream fileInputStream = new FileInputStream(imagePath + name);
            response.setContentType("image/jpeg");
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copy(fileInputStream,outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
