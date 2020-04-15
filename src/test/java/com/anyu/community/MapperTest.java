package com.anyu.community;

import com.anyu.community.entity.DiscussPost;
import com.anyu.community.entity.User;
import com.anyu.community.mapper.DiscussPostMapper;
import com.anyu.community.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    void select(){
        System.out.println(userMapper.selectById(101));
        System.out.println(userMapper.selectByEmail("nowcoder103@sina.com"));
        System.out.println(userMapper.selectByName("liubei"));
    }
    @Test
    void insert(){
        User user=new User();
        user.setUsername("popo");
        userMapper.insertUser(user);
    }
    @Test
    void update(){
        userMapper.updateEmail(150,"2010@qq.com");
        userMapper.updateHeaderUrl(150,"http://images.nowcoder.com/head/149t.png");
        userMapper.updatePassword(150,"123456");
        userMapper.updateStatus(150,1);
    }
    @Test
    void dicussmapper(){
        List<DiscussPost> list=discussPostMapper.selectDiscussPost(0,0,10);
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
        for (DiscussPost d : list
             ) {
            System.out.println(d);
        }
    }
}
