package com.anyu.community.service.impl;

import com.anyu.community.entity.DiscussPost;
import com.anyu.community.service.DiscussPostServicce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostServicce {
    @Autowired
    private BasedClass basedClass;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return basedClass.discussPostMapper.selectDiscussPost(userId, offset, limit);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        return basedClass.discussPostMapper.selectDiscussPostRows(userId);
    }
}
