package com.anyu.community.controller;

import com.anyu.community.entity.DiscussPost;
import com.anyu.community.entity.User;
import com.anyu.community.service.DiscussPostServicce;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostServicce discussPostServicce;
    @Autowired
    private HostHolder hostHolder;

    /**
     * http://localhost:8080/community/discuss/add
     *
     * @param title
     * @param content
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        System.out.println(discussPost);
        discussPostServicce.addDiscussPost(discussPost);
        return CommunityUtil.getJSONString(0, "发布成功");
    }
}
