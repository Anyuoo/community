package com.anyu.community.service;

import com.anyu.community.entity.User;

public interface UserService {
    User findUserById(int id);

    User findUserByName(String username);

    User findUserByEmail(String email);

    int addUser(User user);

    int updateUserStatus(int id, int status);

    int updateUserHeaderUrl(int id, String headerUrl);

    int updateUserPassword(int id, String password);

    int updateUserEmail(int id, String email);
}
