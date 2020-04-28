package com.anyu.community.service.impl;

import com.anyu.community.service.FollowService;
import com.anyu.community.utils.RedisUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;


@Service
public class FollowServiceImpl extends BaseClass implements FollowService {
    @Override
    public void follow(int userId, EntityType entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);

                operations.multi();
                //某实体的粉丝、关注者
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                //某用户关注的实体
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    @Override
    public void cancelFollow(int userId, EntityType entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);

                operations.multi();
                //某实体的粉丝、关注者
                operations.opsForZSet().remove(followerKey, userId);
                //某用户关注的实体
                operations.opsForZSet().remove(followeeKey, entityId);
                return operations.exec();
            }
        });
    }

    @Override
    public boolean followStatus(int userId, EntityType entityType, int entityId) {
        String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);
        Double score = redisTemplate.opsForZSet().score(followeeKey, entityId);
        return score != null;
    }

    @Override
    public long countFollowers(EntityType entityType, int entityId) {
        String followerKey = RedisUtil.getFollowerKey(entityType, entityId);
        Long followerCount = redisTemplate.opsForZSet().size(followerKey);
        return followerCount;
    }

    @Override
    public long countFollowee(int userId, EntityType entityType) {
        String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);
        Long followeeCount = redisTemplate.opsForZSet().size(followeeKey);
        return followeeCount;
    }
}
