package com.anyu.community.controller;

import com.anyu.community.entity.DiscussPost;
import com.anyu.community.entity.Page;
import com.anyu.community.entity.User;
import com.anyu.community.service.DiscussPostServicce;
import com.anyu.community.service.UserService;
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
public class HomePageController {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostServicce discussPostServicce;

    @GetMapping({"/index", "/"})
    public String getIndexPage(Model model, @Validated Page page) {
        //方法调用前SPRINGmvc自动实例化参数对象，并注入
        page.setPath("/index");
        page.setRows(discussPostServicce.findDiscussPostRows(0));
        List<DiscussPost> posts = discussPostServicce.findDiscussPosts(0, page.getOffSet(), 10);
        List<Map<String, Object>> postAndUser = new ArrayList<>();
        if (!posts.isEmpty()) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>(2);
                User user = userService.findUserById(post.getUserId());
                map.put("post", post);
                map.put("user", user);
                postAndUser.add(map);
            }
        }
        model.addAttribute("PostAndUser", postAndUser);
        return "index";
    }
}