package com.anyu.community.service.impl;

import com.anyu.community.entity.User;
import com.anyu.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private BasedClass basedClass;
    @Override
    public User findUserById(int id) {
        return basedClass.userMapper.selectById(id);
    }

    @Override
    public User findUserByName(String username) {
        return basedClass.userMapper.selectByName(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return basedClass.userMapper.selectByEmail(email);
    }

    @Override
    public int addUser(User user) {
        return basedClass.userMapper.insertUser(user);
    }

    @Override
    public int updateUserStatus(int id, int status) {
        return basedClass.userMapper.updateStatus(id,status);
    }

    @Override
    public int updateUserHeaderUrl(int id, String headerUrl) {
        return basedClass.userMapper.updateHeaderUrl(id,headerUrl);
    }

    @Override
    public int updateUserPassword(int id, String password) {
        return basedClass.userMapper.updatePassword(id,password);
    }

    @Override
    public int updateUserEmail(int id, String email) {
        return basedClass.userMapper.updateEmail(id,email);
    }
}
