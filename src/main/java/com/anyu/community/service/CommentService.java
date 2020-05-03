package com.anyu.community.service;

import com.anyu.community.entity.Comment;
import com.anyu.community.utils.CommunityConstant;

import java.util.List;

public interface CommentService extends CommunityConstant {

    List<Comment> findCommentsByEntity(EntityType entityType, int entityId, int offset, int limit);

    int findCommentCount(EntityType entityType, int entityId);

    int addComment(Comment comment);

    Comment findCommentById(int id);
}
