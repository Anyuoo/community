package com.anyu.community.service;

import com.anyu.community.entity.LoginTicket;
import com.anyu.community.entity.User;

import java.util.Map;

public interface UserService {
    User findByName(String username);

    User findUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    Map<String, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    int updateUserHeaderUrl(int userId, String headerUrl);

    boolean updateUserPassword(String oldPassword, String newPassword);

    void saveKaptcha(String owner, String text);

    String getKaptcha(String owner);

}
