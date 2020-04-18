package com.anyu.community.utils;

import com.anyu.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，代替session
 */

@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    /**
     * 根据当前线程id获取值
     *
     * @return
     */
    public User getUser() {
        return users.get();
    }

    /**
     * 存入user对象
     *
     * @param user
     */
    public void setUser(User user) {
        users.set(user);
    }

    /**
     * 删除当前缓存
     */
    public void deleteUser() {
        users.remove();
    }


}
