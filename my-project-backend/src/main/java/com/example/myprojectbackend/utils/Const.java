package com.example.myprojectbackend.utils;

public class Const {
    // JWT令牌
    public final static String JWT_BLACK_LIST = "jwt:blacklist:";
    public final static String JWT_FREQUENCY = "jwt:frequency:";
    // 请求频率限制
    public final static String FLOW_LIMIT_COUNTER = "flow:counter:";
    public final static String FLOW_LIMIT_BLOCK = "flow:block:";
    // 邮件验证码
    public final static String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    public final static String VERIFY_EMAIL_DATA = "verify:email:data:";
    // 过滤器优先级
    public final static int ORDER_LIMIT = -101;
    public final static int ORDER_CORS = -102;
    // 请求自定义属性
    public final static String ATTR_USER_ID = "id";
    // 消息队列
    public final static String MQ_MAIL = "mail";
    // 用户角色
    public final static String ROLE_DEFAULT = "user";
    // 论坛相关
    // 论坛天气缓存
    public final static String FORUM_WEATHER_CACHE = "weather:cache:";
    // 论坛图片缓存
    public final static String FORUM_IMAGE_COUNTER = "forum:image:";

    public final static String FORUM_TOPIC_CREATE_COUNTER = "forum:topic:create:";

    public final static String FORUM_TOPIC_COMMENT_COUNTER = "forum:topic:comment:";

    public final static String FORUM_TOPIC_PREVIEW_CACHE = "topic:preview:";
}
