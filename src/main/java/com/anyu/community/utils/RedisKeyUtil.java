package com.anyu.community.utils;

public class RedisKeyUtil implements CommunityConstant {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    /**
     * 给某实体点赞key
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return key (like:entity:entityType:entityId) value (set(entityId))
     */
    public static String getEntityLikeKey(EntityType entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType.name() + SPLIT + entityId;
    }

    /**
     * 给用户点赞key
     *
     * @param userId
     * @return
     */
    @Deprecated
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个实体的粉丝
     *
     * @param entityType
     * @param entityId
     * @return key-->follower:entityType:entityId  value-->zSet(userId,NOW)
     */
    public static String getFollowerKey(EntityType entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType.name() + SPLIT + entityId;
    }

    /**
     * 某个用户关注的实体
     *
     * @param userId
     * @param entityType
     * @return key-->followee:userId:entityType  value-->zSet(entityId,NOW)
     */
    public static String getFolloweeKey(int userId, EntityType entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType.name();
    }

    /**
     * 获取某一个用户的验证码
     *
     * @param owner 用户登录临时凭证
     * @return key
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 某个用户登录凭证
     *
     * @param ticket
     * @return
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 用户的key
     *
     * @param userId
     * @return
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
