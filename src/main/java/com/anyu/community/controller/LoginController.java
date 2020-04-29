package com.anyu.community.controller;

import com.anyu.community.entity.User;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CommunityConstant;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.CookieUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final String PREFIX = "site/";

    @Autowired
    private UserService userService;
    @Autowired
    private Producer producer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/login")
    public String getLoginPage() {
        return PREFIX + "login";
    }

    /**
     * 用户登录
     *
     * @param user       传入的user
     * @param result     数据效验结果集
     * @param code       输入的验证码
     * @param remembered 是否勾选记住我
     * @param model
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/login")
    public String login(@Validated User user, BindingResult result, String code, boolean remembered, Model model, HttpServletRequest request, HttpServletResponse response) {
        //输入的user数据不合法
        if (result.hasFieldErrors("username")) {
            model.addAttribute("usernameLoginMsg", result.getFieldError("username").getDefaultMessage());
            return PREFIX + "login";
        }
        if (result.hasFieldErrors("password")) {
            model.addAttribute("passwordLoginMsg", result.getFieldError("password").getDefaultMessage());
            return PREFIX + "login";
        }

        //得到临时凭证
        String kaptchaOwner = CookieUtil.getCookieValue(request, "kaptchaOwner");
        //验证码是否正确
//        String currentCode = (String) session.getAttribute("kaptcha");
        String currentCode = userService.getKaptcha(kaptchaOwner);
        if (StringUtils.isBlank(currentCode) || !currentCode.equalsIgnoreCase(code)) {
            model.addAttribute("kaptchaMsg", "验证码错误");
            return PREFIX + "login";
        }

        //是否勾选记住
        int expiredSeconds = remembered ? REMEMBER_EXPIRED_SECOND : DEFAULT_EXPIRED_SECOND;
        Map<String, Object> map = userService.login(user.getUsername(), user.getPassword(), expiredSeconds);

        //检查账号是否存在，和激活
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("usernameLoginMsg", map.get("usernameLoginMsg"));
            model.addAttribute("passwordLoginMsg", map.get("passwordLoginMsg"));
            return PREFIX + "login";
        }
    }

    /**
     * 退出登录
     *
     * @param ticket
     * @return
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/index";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return PREFIX + "register";
    }

    /**
     * 注册
     *
     * @param model
     * @param user
     * @param result s数据验证不合法信息
     * @return
     */
    @PostMapping("/register")
    public String register(Model model, @Validated User user, BindingResult result) {
        //user数据不合法
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                model.addAttribute(error.getField() + "Msg", error.getDefaultMessage());
            }
            return PREFIX + "register";
        }
        //用户存在
        Map<String, Object> map = userService.register(user);
        if (!map.isEmpty()) {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return PREFIX + "register";
        }
        //成功注册
        model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了激活邮件，请尽快激活！");
        model.addAttribute("target", "/index");
        return PREFIX + "operate-result";
    }

    /**
     * 接受验证邮箱激活
     *
     * @param userId
     * @param code
     * @param model
     * @return http://localhost:8080/community/activation/userId/code
     * http://localhost:8080/community/activation/168/417ab9e8903747afbd51cfa9e46447b0
     */
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

    /**
     * 二维码生成和更新
     *
     * @param response
     */
    @GetMapping("/kaptcha")
    public void kaptcha(HttpServletResponse response) {
        //生成二维码字符和图片
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        //将验证码存入session
        // session.setAttribute("kaptcha", text);

        //验证码归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        //存入验证码信息
        userService.saveKaptcha(kaptchaOwner, text);

        //将图片发送至浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("二维码生成失败", e.getMessage());
        }

    }
}
