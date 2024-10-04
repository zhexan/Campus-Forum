package com.example.myprojectbackend.entity.vo.response;

import lombok.Data;

@Data
public class FindPrivacyVO {
    boolean phone;
    boolean email;
    boolean wechat;
    boolean qq;
    boolean gender;
}
