package com.anyu.community.service;

import com.anyu.community.entity.User;

import java.util.Map;

public interface UserService {
    User findUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

    User findUserByName(String username);

    User findUserByEmail(String email);

    int updateUserStatus(int id, int status);

    int updateUserHeaderUrl(int id, String headerUrl);

    int updateUserPassword(int id, String password);

    int updateUserEmail(int id, String email);
}
