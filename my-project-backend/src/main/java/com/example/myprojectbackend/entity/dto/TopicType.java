package com.example.myprojectbackend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.myprojectbackend.entity.BaseData;

@TableName("db_topic_type")
public class TopicType implements BaseData {
    Integer id;
    String name;
    @TableField("`desc`")
    String desc;
}
