package com.example.myprojectbackend.controller;

import com.example.myprojectbackend.entity.RestBean;
import com.example.myprojectbackend.service.ImageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Resource
    private ImageService imageService;
    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file") MultipartFile file,
                                   @RequestAttribute("id") int id) throws IOException {
        if (file.getSize() > 1024 * 100) {
            return RestBean.failure(400, "头像大小不能超过100kb");
        }
        log.info("正在进行头像上传操作");
        String url = imageService.uploadAvatar(file, id);
        if(url != null) {
            log.info("头像上传成功" + file.getSize());
            return RestBean.success(url);
        } else {
            return RestBean.failure(400, "头像上传失败");
        }
    }
}
