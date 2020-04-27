package com.anyu.community.service;

import com.anyu.community.utils.CommunityConstant;

public interface LikeService extends CommunityConstant {

    /**
     * 对某实体点赞
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    void like(int userId, EntityType entityType, int entityId);

    /**
     * 统计某实体点赞数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    long countLike(EntityType entityType, int entityId);


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
