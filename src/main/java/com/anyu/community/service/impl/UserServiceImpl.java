package com.anyu.community.service.impl;

import com.anyu.community.entity.LoginTicket;
import com.anyu.community.entity.User;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CommunityConstant;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.RedisKeyUtil;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends BaseClass implements UserService, CommunityConstant {

    @Override
    public User findByName(String username) {
        return userMapper.selectByName(username);
    }

    /**
     * 查找用户
     *
     * @param id
     * @return
     */
    @Override
    public User findUserById(int id) {
        //return userMapper.selectById(id);
        User user = getUserCache(id);
        if (user == null)
            user = initUserCache(id);
        return user;
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
     *
     * @param userId
     * @param code
     * @return
     */
    @Override
    public int activation(int userId, String code) {
        User user = findUserById(userId);
        if (user.getStatus() == 1)
            return ACTIVATION_REPEAT;
        else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            //删除缓存
            clearUserCache(userId);
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
        //    loginTicketMapper.insertLoginTicket(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

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
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
    }

    /**
     * 查询LoginTicket
     *
     * @param ticket
     * @return
     */
    @Override
    public LoginTicket findLoginTicket(String ticket) {
        // return loginTicketMapper.selectLoginTicketByTicket(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    /**
     * 更新头像
     *
     * @param userId
     * @param headerUrl
     * @return
     */
    @Override
    public int updateUserHeaderUrl(int userId, String headerUrl) {
        //删除缓存
        clearUserCache(userId);
        return userMapper.updateHeaderUrl(userId, headerUrl);
    }

    /**
     * 更新密码
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Override
    public boolean updateUserPassword(String oldPassword, String newPassword) {
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!oldPassword.equals(user.getPassword()))
            return false;
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(user.getId(), newPassword);
        //删除缓存
        clearUserCache(user.getId());
        return true;
    }

    /**
     * 存入验证码信息
     *
     * @param owner 验证码所有者，临时凭证
     * @param text  验证码信息
     */
    @Override
    public void saveKaptcha(String owner, String text) {
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(owner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);
    }

    /**
     * 获取二维码信息
     *
     * @param owner
     * @return
     */
    @Override
    public String getKaptcha(String owner) {
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(owner);
        return (String) redisTemplate.opsForValue().get(kaptchaKey);
    }

    /**
     * 将用户信息存入缓存
     * 1.优先从缓存中获取用户信息
     * 2.缓存中没有，再去数据库中查寻
     * 3.用户信息改变时，删除缓存
     */


    /**
     * 1.优先从缓存中获取用户信息
     *
     * @param userId
     * @return
     */
    private User getUserCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    /**
     * 2.缓存中没有，再去数据库中查寻
     *
     * @param userId
     * @return
     */
    private User initUserCache(int userId) {
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 3.用户信息改变时，删除缓存
     *
     * @param userId
     */
    private void clearUserCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
