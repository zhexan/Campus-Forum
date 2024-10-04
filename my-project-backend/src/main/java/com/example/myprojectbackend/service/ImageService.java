package com.example.myprojectbackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

public interface ImageService {
    void fetchImageFromMinio(OutputStream outputStream, String image) throws Exception;
    String uploadAvatar(MultipartFile multipartFile, int id) throws IOException;
}
