package com.anyu.community.service.impl;

import com.anyu.community.entity.LoginTicket;
import com.anyu.community.entity.User;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CommunityConstant;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl extends BasedClass implements UserService, CommunityConstant {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine engine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 查找用户
     *
     * @param id
     * @return
     */
    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>(1);
        if (user == null)
            throw new IllegalArgumentException("参数不能为空");
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "邮箱已注册");
            return map;
        }
        //注册用户
        //用于密码加密，随机字符串
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //http://localhost:8080/community/activation/id/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = engine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }


    /**
     * 邮箱激活
     * @param userId
     * @param code
     * @return
     */
    @Override
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1)
            return ACTIVATION_REPEAT;
        else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>(1);
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameLoginMsg", "用户不存在");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameLoginMsg", "账号未激活");
            return map;
        }
        String pw = CommunityUtil.md5(password + user.getSalt());
        //密码是否正确
        if (!user.getPassword().equals(pw)) {
            map.put("passwordLoginMsg", "密码错误");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        String ticket = CommunityUtil.generateUUID();
        loginTicket.setTicket(ticket);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicket.setStatus(0);
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", ticket);
        return map;
    }

    /**
     * 注销登录
     *
     * @param ticket
     */
    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket,1);
    }
}
