package com.example.myprojectbackend.entity.vo.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class AccountDetailsVo {
    int gender;
    String phone;
    String qq;
    String wechat;
    String desc;

}
