package com.example.myprojectbackend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_topic")
public class Topic {
    private Integer id;
    private String title;
    private String content;
    private int type;
    private Date time;
    Integer uid;
    String username;
    String avatar;


}
