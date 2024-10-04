package com.example.myprojectbackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    String uploadAvatar(MultipartFile multipartFile, int id) throws IOException;
}
