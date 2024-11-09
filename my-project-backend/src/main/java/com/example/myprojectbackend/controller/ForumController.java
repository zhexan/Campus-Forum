package com.example.myprojectbackend.controller;

import com.example.myprojectbackend.entity.RestBean;
import com.example.myprojectbackend.entity.vo.response.TopicTypeVO;
import com.example.myprojectbackend.entity.vo.response.WeatherVO;
import com.example.myprojectbackend.service.TopicService;
import com.example.myprojectbackend.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;
    @Resource
    TopicService topicService;

    @GetMapping("weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude) {
        WeatherVO vo = weatherService.fetchWeather(longitude, latitude);
        return vo == null ?
                RestBean.failure(400, "获取天气信息失败")
                : RestBean.success(vo);

    }
    @GetMapping("type")
    public RestBean<List<TopicTypeVO>> type() {
        return RestBean.success(topicService.getTopicTypes()
                .stream()
                .map(type -> type.asViewObject(TopicTypeVO.class))
                .toList());
    }
    
}
