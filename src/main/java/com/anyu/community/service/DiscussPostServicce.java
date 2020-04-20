package com.anyu.community.service;

import com.anyu.community.entity.DiscussPost;

import java.util.List;

public interface DiscussPostServicce {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    int findDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost discussPost);
}
