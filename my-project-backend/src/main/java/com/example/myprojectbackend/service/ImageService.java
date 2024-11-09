package com.example.myprojectbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myprojectbackend.entity.dto.ImageStore;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

public interface ImageService extends IService<ImageStore> {
    void fetchImageFromMinio(OutputStream outputStream, String image) throws Exception;
    String uploadImage(MultipartFile file, int id) throws IOException;
    String uploadAvatar(MultipartFile multipartFile, int id) throws IOException;
}
