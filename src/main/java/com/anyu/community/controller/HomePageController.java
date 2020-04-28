package com.anyu.community.controller;

import com.anyu.community.entity.DiscussPost;
import com.anyu.community.entity.Page;
import com.anyu.community.entity.User;
import com.anyu.community.service.DiscussPostServicce;
import com.anyu.community.service.LikeService;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomePageController implements CommunityConstant {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostServicce discussPostServicce;
    @Autowired
    private LikeService likeService;

    @GetMapping({"/index", ""})
    public String getIndexPage(Model model, @Validated Page page) {
        //方法调用前SPRINGmvc自动实例化参数对象，并注入
        page.setPath("/index");
        page.setRows(discussPostServicce.findDiscussPostRows(0));
        List<DiscussPost> posts = discussPostServicce.findDiscussPosts(0, page.getOffset(), 10);
        List<Map<String, Object>> postAndUser = new ArrayList<>();
        if (!posts.isEmpty()) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>(2);
                User user = userService.findUserById(post.getUserId());
                map.put("post", post);
                map.put("user", user);
                //赞
                long likeCount = likeService.countLike(EntityType.POST, post.getId());
                map.put("likeCount", likeCount);
                postAndUser.add(map);
            }
        }
        model.addAttribute("PostAndUser", postAndUser);
        return "index";
    }

    /**
     * 返回错误页面
     *
     * @return
     */
    @GetMapping("/error")
    public String getErrorPage() {
        return "error/500";
    }
}