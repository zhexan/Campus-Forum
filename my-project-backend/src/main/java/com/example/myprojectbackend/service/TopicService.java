package com.example.myprojectbackend.service;

import com.example.myprojectbackend.entity.dto.TopicType;

import java.util.List;

public interface TopicService {
    List<TopicType> getTopicTypes();
}
