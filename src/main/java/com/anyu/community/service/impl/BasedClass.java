package com.anyu.community.service.impl;

import com.anyu.community.mapper.DiscussPostMapper;
import com.anyu.community.mapper.LoginTicketMapper;
import com.anyu.community.mapper.UserMapper;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasedClass {
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected DiscussPostMapper discussPostMapper;
    @Autowired
    protected LoginTicketMapper loginTicketMapper;
    @Autowired
    protected HostHolder hostHolder;
}