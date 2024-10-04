package com.example.myprojectbackend.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AccountVo {
    String email;
    String username;
    String role;
    String avatar;
    Date registerTime;
}
