package com.example.myprojectbackend.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.myprojectbackend.entity.dto.Account;
import com.example.myprojectbackend.mapper.AccountMapper;
import com.example.myprojectbackend.service.ImageService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {
    @Resource
    private MinioClient client;
    @Resource
    private AccountMapper mapper;

    @Override
    public void fetchImageFromMinio(OutputStream outputStream, String image) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("study")
                .object(image)
                .build();
        GetObjectResponse response = client.getObject(args);
        IOUtils.copy(response, outputStream);
    }

    @Override
    public String uploadAvatar(MultipartFile multipartFile, int id) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "");
        imageName = "/avatar/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                .object(imageName)
                .build();
        try {
            client.putObject(args);
            if(mapper.update(null, Wrappers.<Account>update().eq("id", id).set("avatar", imageName)) > 0) {
                return imageName;
            } else
                return null;
        } catch (Exception e) {
            log.info("头像上传失败");
            return null;
        }
    }
}
