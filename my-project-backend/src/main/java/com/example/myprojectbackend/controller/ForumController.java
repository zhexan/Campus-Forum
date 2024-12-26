package com.example.myprojectbackend.controller;

import com.example.myprojectbackend.entity.RestBean;
import com.example.myprojectbackend.entity.dto.Topic;
import com.example.myprojectbackend.entity.vo.request.TopicCreateVO;
import com.example.myprojectbackend.entity.vo.response.TopicPreviewVO;
import com.example.myprojectbackend.entity.vo.response.TopicTypeVO;
import com.example.myprojectbackend.entity.vo.response.WeatherVO;
import com.example.myprojectbackend.service.TopicService;
import com.example.myprojectbackend.service.WeatherService;
import com.example.myprojectbackend.utils.Const;
import com.example.myprojectbackend.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;
    @Resource
    TopicService topicService;
    @Resource
    ControllerUtils utils;
    @GetMapping("weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude) {
        WeatherVO vo = weatherService.fetchWeather(longitude, latitude);
        return vo == null ?
                RestBean.failure(400, "获取天气信息失败")
                : RestBean.success(vo);

    }
    @GetMapping("types")
    public RestBean<List<TopicTypeVO>> type() {
        return RestBean.success(topicService.getTopicTypes()
                .stream()
                .map(type -> type.asViewObject(TopicTypeVO.class))
                .toList());
    }
    @PostMapping("create_topic")
    public RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id) {
        return utils.messageHandle(() -> topicService.createTopic(id, vo));
    }
    @GetMapping("list-topic")
    public RestBean<List<TopicPreviewVO>> listTopic(@RequestParam @Min(0) int page,
                                                    @RequestParam @Min(0) int type) {
        return RestBean.success(topicService.listTopicByPage(page, type));
    }
    
}
