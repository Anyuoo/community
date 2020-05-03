package com.anyu.community.service.impl;

import com.anyu.community.entity.Comment;
import com.anyu.community.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * 评论服务
 */
@Service
public class CommentServiceImpl extends BaseClass implements CommentService {


    @Override
    public List<Comment> findCommentsByEntity(EntityType entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType.value(), entityId, offset, limit);
    }

    @Override
    public int findCommentCount(EntityType entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType.value(), entityId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null)
            new IllegalArgumentException("參數不能爲空");
        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);
        if (comment.getEntityType() == EntityType.POST.value()) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getTargetId(), count + 1);
        }
        return rows;
    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
