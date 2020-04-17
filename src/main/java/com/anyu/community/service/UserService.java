package com.anyu.community.service;

import com.anyu.community.entity.User;

import java.util.Map;

public interface UserService {
    User findUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    Map<String, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

}
