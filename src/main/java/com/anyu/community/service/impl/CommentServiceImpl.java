package com.anyu.community.service.impl;

import com.anyu.community.entity.Comment;
import com.anyu.community.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务
 */
@Service
public class CommentServiceImpl extends BasedClass implements CommentService {


    @Override
    public List<Comment> findCommentsByEntity(EntityType entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType.value(), entityId, offset, limit);
    }

    @Override
    public int findCommentCount(EntityType entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType.value(), entityId);
    }
}
