package com.example.myprojectbackend.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myprojectbackend.entity.dto.Topic;
import com.example.myprojectbackend.entity.dto.TopicType;
import com.example.myprojectbackend.entity.vo.request.TopicCreateVO;
import com.example.myprojectbackend.entity.vo.response.TopicPreviewVO;
import com.example.myprojectbackend.mapper.TopicMapper;
import com.example.myprojectbackend.mapper.TopicTypeMapper;
import com.example.myprojectbackend.service.TopicService;
import com.example.myprojectbackend.utils.CacheUtils;
import com.example.myprojectbackend.utils.Const;
import com.example.myprojectbackend.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Resource
    private TopicTypeMapper topicTypeMapper;
    @Resource
    private FlowUtils flowUtils;
    @Resource
    private CacheUtils cacheUtils;
    @PostConstruct
    public void initTypes() {
        types = topicTypeMapper.selectList(null)
                .stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());
    }
    // 文章类型预处理
    private Set<Integer> types = null;

    @Override
    public List<TopicType> getTopicTypes() {
        return topicTypeMapper.selectList(null);
    }


       @Override
    public String createTopic(int uid, TopicCreateVO vo) {
        if(!this.textLimitCheck(vo.getContent()))
            return "内容过长,发文失败";
        if(!types.contains(vo.getType()))
            return "文章类型非法";
        String key = Const.FORUM_TOPIC_CREATE_COUNTER + uid;
        if(!flowUtils.limitPeriodCounterCheck(key, 3, 3600))
            return "操作过于频繁,请稍后再试";
        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());
        if (this.save(topic)) {
            cacheUtils.deleteCache(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            return null;
        } else {
            return "内部错误，请联系管理员";
        }
    }

    /**
     * 根据页码和类型获取文章列表
     * @param page 页码
     * @param type 文章类型
     * @return
     */
    @Override
    public List<TopicPreviewVO> listTopicByPage(int page, int type) {
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + page + ":" + type;
        List<TopicPreviewVO> cache = cacheUtils.takeListFromCache(key, TopicPreviewVO.class);
        if (cache != null) {
            return cache;
        }
        List<TopicPreviewVO> list;
        List<Topic> topics;
        if(type == 0) {
            topics = baseMapper.topicList(page * 10);
        } else {
           topics = baseMapper.topicListByType(page * 10, type);
        }
        if(topics.isEmpty()) return null;
        list = topics.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 20);
        return null;

    }

    private TopicPreviewVO resolveToPreview(Topic topic) {
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(topic, vo);
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if(insert instanceof String) {
                if(previewText.length() > 300) continue;
                previewText.append(insert);
            } else if(insert instanceof Map<?, ?> map) {
                Optional.ofNullable(map.get("image")).ifPresent(obj -> images.add(obj.toString()));
            }
        }
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        return vo;
    }
    /**
     * 文章计数限制，超过两万字，无法发表
     * @param object 文章内容
     * @return 无文章内容或文章字数超过两万返回false
     */
    private boolean textLimitCheck(JSONObject object) {
        if(object == null) {
            return false;
        }
        long length = 0;
        for (Object ops : object.getJSONArray("ops")) {
            length += JSONObject.from(ops).getString("insert").length();
            if (length > 20000) {
                return false;
            }
        }
        return true;
    }
}
