package com.anyu.community.service;

import com.anyu.community.utils.CommunityConstant;

public interface LikeService extends CommunityConstant {

    /**
     * 点赞
     *
     * @param userId       点赞用户
     * @param entityType   被点赞实体
     * @param entityId     被点赞实体id
     * @param entityUserId 备点赞用户
     */
    void like(int userId, EntityType entityType, int entityId, int entityUserId);

    /**
     * 统计某实体点赞数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    long countLike(EntityType entityType, int entityId);

    /**
     * 统计用户点赞数量
     *
     * @param userId
     * @return
     */
    int countUserLike(int userId);

    /**
     * 查询某人是否对某实体点赞
     *
     * @param entityType
     * @param entityId
     * @param userId
     * @return
     */
    int findEntityLikeStatus(EntityType entityType, int entityId, int userId);
}
