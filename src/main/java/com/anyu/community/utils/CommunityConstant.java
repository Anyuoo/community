package com.anyu.community.utils;

public interface CommunityConstant {
    //邮件激活，激活成功
    int ACTIVATION_SUCCESS = 0;
    //邮件激活，重复激活
    int ACTIVATION_REPEAT = 1;
    //邮件激活，激活失败
    int ACTIVATION_FAILURE = 2;


    //默认状态登录凭证的状态时间
    int DEFAULT_EXPIRED_SECOND = 3600 * 12;
    //勾选记住状态下的登录凭证时间
    int REMEMBER_EXPIRED_SECOND = 3600 * 24 * 100;
}
