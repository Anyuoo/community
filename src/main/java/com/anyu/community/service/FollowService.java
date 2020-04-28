package com.anyu.community.service;

import com.anyu.community.utils.CommunityConstant;

public interface FollowService extends CommunityConstant {

    /**
     * 关注
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    void follow(int userId, EntityType entityType, int entityId);

    /**
     * 取消关注
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    void cancelFollow(int userId, EntityType entityType, int entityId);

    /**
     * 用户是否关注该实体
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    boolean followStatus(int userId, EntityType entityType, int entityId);

    /**
     * 某实体的粉丝数量
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return 粉丝数量
     */
    long countFollowers(EntityType entityType, int entityId);

    /**
     * 某个用户关注的实体
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @return
     */
    long countFollowee(int userId, EntityType entityType);
}
