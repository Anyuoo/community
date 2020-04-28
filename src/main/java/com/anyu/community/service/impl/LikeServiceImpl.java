package com.anyu.community.service.impl;

import com.anyu.community.service.LikeService;
import com.anyu.community.utils.RedisUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends BaseClass implements LikeService {

    /**
     * 点赞
     *
     * @param userId       点赞用户
     * @param entityType   被点赞实体
     * @param entityId     被点赞实体id
     * @param entityUserId 备点赞用户
     */
    @Override
    public void like(int userId, EntityType entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisUtil.getUserLikeKey(entityUserId);
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                //事务
                operations.multi();
                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    @Override
    public int countUserLike(int userId) {
        String userLikeKey = RedisUtil.getUserLikeKey(userId);
        Integer userLikeCount = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return userLikeCount == null ? 0 : userLikeCount.intValue();
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
