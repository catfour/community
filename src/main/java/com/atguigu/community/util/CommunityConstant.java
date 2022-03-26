package com.atguigu.community.util;

public interface CommunityConstant {
    //激活成功
    public  int ACTIVATION_SUCCESS = 0;

    //重复激活
    public  int ACTIVATION_REPEAT = 1;

    //激活无效
    public  int ACTIVATION_FAILURE = 2;

    //默认失效时间
    public  int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    //rememberMe 失效时间
    public  int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    //实体类型：帖子
    int ENTITY_TYPE_POST = 1;
    //实体类型:评论
    int ENTITY_TYPE_COMMENT = 2;
}
