package com.example.myprojectbackend.service.Impl;

import com.example.myprojectbackend.entity.dto.TopicType;
import com.example.myprojectbackend.mapper.TopicTypeMapper;
import com.example.myprojectbackend.service.TopicService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    @Resource
    private TopicTypeMapper topicTypeMapper;
    @Override
    public List<TopicType> getTopicTypes() {
        return topicTypeMapper.selectList(null);
    }
}
