package com.anyu.community.controller;

import com.anyu.community.entity.User;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    private final static String PREFIX = "site/";
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String getLoginPage() {
        return PREFIX + "login";
    }

    @PostMapping("/login")
    public String login(Model model, User user) {

        return "index";
    }

    /**
     * 注册
     *
     * @return
     */
    @GetMapping("/register")
    public String getRegisterPage() {
        return PREFIX + "register";
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return PREFIX + "operate-result";
        }
        model.addAttribute("usernameMsg", map.get("usernameMsg"));
        model.addAttribute("passwordMsg", map.get("passwordMsg"));
        model.addAttribute("emailMsg", map.get("emailMsg"));
        return PREFIX + "register";
    }

    /**
     * 接受验证邮箱激活
     *
     * @param userId
     * @param code
     * @param model
     * @return
     */
    //http://localhost:8080/community/activation/userId/code
    //http://localhost:8080/community/activation/168/417ab9e8903747afbd51cfa9e46447b0
    @GetMapping("/activation/{userId}/{code}")
    public String activate(@PathVariable("userId") int userId, @PathVariable("code") String code, Model model) {
        int status = userService.activation(userId, code);
        if (status == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "您的账号已经激活成功,可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (status == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "邮箱已经激活过了！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "账号邮箱激活失败！");
            model.addAttribute("target", "/index");
        }
        return PREFIX + "operate-result";
    }
}
