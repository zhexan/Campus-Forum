package com.example.myprojectbackend.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myprojectbackend.entity.dto.Topic;
import com.example.myprojectbackend.entity.vo.response.TopicPreviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
    @Select("""
             select * from db_topic left join db_account on db_topic.uid = db_account.id
             order by `time` desc limit ${page}, 10
             """)
    List<Topic> topicList(int page);
    @Select("""
             select * from db_topic left join db_account on db_topic.uid = db_account.id
             where `type` = ${type}
             order by `time` desc limit ${page}, 10
             """)
    List<Topic> topicListByType(int page, int type);
}
