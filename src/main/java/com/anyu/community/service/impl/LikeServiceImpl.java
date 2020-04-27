package com.anyu.community.service.impl;

import com.anyu.community.service.LikeService;
import com.anyu.community.utils.RedisUtil;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends BaseClass implements LikeService {

    /**
     * 点赞
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    @Override
    public void like(int userId, EntityType entityType, int entityId) {
        String entityLikeKey = RedisUtil.getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if (isMember) {
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        } else
            redisTemplate.opsForSet().add(entityLikeKey, userId);
    }

    @Override
    public long countLike(EntityType entityType, int entityId) {
        String entityLikeKey = RedisUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * @param entityType
     * @param entityId
     * @param userId
     * @return 1 已点赞 0 为点赞
     */
    @Override
    public int findEntityLikeStatus(EntityType entityType, int entityId, int userId) {
        String entityLikeKey = RedisUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
}
