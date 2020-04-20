package com.anyu.community.service.impl;

import com.anyu.community.mapper.DiscussPostMapper;
import com.anyu.community.mapper.LoginTicketMapper;
import com.anyu.community.mapper.UserMapper;
import com.anyu.community.utils.HostHolder;
import com.anyu.community.utils.MailClient;
import com.anyu.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.TemplateEngine;


public class BasedClass {
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected DiscussPostMapper discussPostMapper;
    @Autowired
    protected LoginTicketMapper loginTicketMapper;
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
