package com.anyu.community.service.impl;

import com.anyu.community.mapper.*;
import com.anyu.community.utils.HostHolder;
import com.anyu.community.utils.MailClient;
import com.anyu.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.TemplateEngine;


public class BaseClass {
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected DiscussPostMapper discussPostMapper;
    @Autowired
    protected LoginTicketMapper loginTicketMapper;
    @Autowired
    protected CommentMapper commentMapper;
    @Autowired
    protected MessageMapper messageMapper;
    @Autowired
    protected HostHolder hostHolder;
    @Autowired
    protected SensitiveFilter sensitiveFilter;
    @Autowired
    protected MailClient mailClient;
    @Autowired
    protected TemplateEngine engine;
    @Value("${community.path.domain}")
    protected String domain;
    @Value("${server.servlet.context-path}")
    protected String contextPath;
}
