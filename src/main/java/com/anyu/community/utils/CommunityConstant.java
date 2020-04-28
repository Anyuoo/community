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


    /**
     * 帖子类型: POST:帖子 COMMENT:评论
     */
    enum EntityType {
        POST("帖子", 1), COMMENT("评论", 2), USER("用户", 3);
        private final String key;
        private final int value;

        EntityType(String key, int value) {
            this.key = key;
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public String key() {
            return this.key;
        }
    }
}
