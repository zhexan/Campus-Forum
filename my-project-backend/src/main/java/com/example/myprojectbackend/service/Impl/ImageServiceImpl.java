package com.example.myprojectbackend.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myprojectbackend.entity.dto.Account;
import com.example.myprojectbackend.entity.dto.ImageStore;
import com.example.myprojectbackend.mapper.AccountMapper;
import com.example.myprojectbackend.mapper.ImageStoreMapper;
import com.example.myprojectbackend.service.ImageService;
import com.example.myprojectbackend.utils.Const;
import com.example.myprojectbackend.utils.FlowUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl extends ServiceImpl<ImageStoreMapper, ImageStore> implements ImageService {
    @Resource
    private MinioClient client;
    @Resource
    private AccountMapper mapper;
    @Resource
    private FlowUtils flowUtils;

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
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
    public String uploadImage(MultipartFile file, int id) throws IOException {
        String key = Const.FORUM_IMAGE_COUNTER + id;
        if(!flowUtils.limitPeriodCounterCheck(key, 20, 3600))
            return null;
        String imageName = UUID.randomUUID().toString().replace("-", "");
        Date date = new Date();
        imageName = "/cache/" + format.format(date) + "/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();
        try {
            client.putObject(args);
            if(this.save(new ImageStore(id, imageName, date))) {
                return imageName;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("图片上传出现问题: "+ e.getMessage(), e);
            return null;
        }
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
            String avatar = mapper.selectById(id).getAvatar();
            this.deleteOldAvatar(avatar);
            if(mapper.update(null, Wrappers.<Account>update().eq("id", id).set("avatar", imageName)) > 0) {
                return imageName;
            } else
                return null;
        } catch (Exception e) {
            log.info("头像上传出现问题{}", e.getMessage(), e);
            return null;
        }
    }
    private void deleteOldAvatar(String avatar) throws Exception{
        if(avatar == null || avatar.isEmpty()) return;
        client.removeObject(RemoveObjectArgs.builder()
                .bucket("study")
                .object(avatar)
                .build());
    }
}
