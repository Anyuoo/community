package com.anyu.community.utils;

public class RedisUtil implements CommunityConstant {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

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
}
