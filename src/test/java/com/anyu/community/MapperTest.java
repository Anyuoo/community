package com.anyu.community;

import com.anyu.community.entity.*;
import com.anyu.community.mapper.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    void messagetest() {
        int count = messageMapper.countConversations(111);
        List<Message> list = messageMapper.listConversations(111, 0, count);
        int n = 0;
        Message message = null;
        for (Message m :
                list) {
            message = m;
            System.out.println("第" + (++n) + "条:" + m);
        }
        message.setToId(175);
        message.setId(0);

        for (int i = 0; i < 10; i++) {
            messageMapper.insertMessage(message);

        }
    }


    @Test
    void commentmapperTest() {
        int rows = commentMapper.selectCountByEntity(1, 228);
        List<Comment> list = commentMapper.selectCommentByEntity(1, 228, 0, rows);
        for (Comment comment : list) {
            System.out.println(comment);
        }
    }

    @Test
    void loginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setExpired(new Date());
        loginTicket.setStatus(1);
        loginTicket.setTicket("1222333");
        loginTicket.setUserId(123);
        loginTicketMapper.insertLoginTicket(loginTicket);
        loginTicketMapper.updateStatus("1222333", 0);
        System.out.println(loginTicketMapper.selectLoginTicketByTicket("1222333"));

    }

    @Test
    void select() {
        System.out.println(userMapper.selectById(101));
        System.out.println(userMapper.selectByEmail("nowcoder103@sina.com"));
        System.out.println(userMapper.selectByName("liubei"));
    }

    @Test
    void insert() {
        User user = new User();
        user.setUsername("popo");
        userMapper.insertUser(user);
    }

    @Test
    void update() {
        userMapper.updateEmail(150, "2010@qq.com");
        userMapper.updateHeaderUrl(150, "http://images.nowcoder.com/head/149t.png");
        userMapper.updatePassword(150, "123456");
        userMapper.updateStatus(150, 1);
    }

    @Test
    void dicussmapper() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPost(0, 0, 10);
        System.out.println(discussPostMapper.selectDiscussPostRows(0));
        for (DiscussPost d : list
        ) {
            System.out.println(d);
        }
    }

    @Test
    void distAddDiscuss() {
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("title");
        discussPost.setContent("content");
        discussPost.setUserId(1);
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);
    }
}
