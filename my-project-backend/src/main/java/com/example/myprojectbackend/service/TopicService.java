package com.example.myprojectbackend.service;

import com.example.myprojectbackend.entity.dto.TopicType;
import com.example.myprojectbackend.entity.vo.request.TopicCreateVO;
import com.example.myprojectbackend.entity.vo.response.TopicPreviewVO;

import java.util.List;

public interface TopicService {
    List<TopicType> getTopicTypes();
    String createTopic(int uid, TopicCreateVO vo);
    List <TopicPreviewVO> listTopicByPage(int page, int type);
}
