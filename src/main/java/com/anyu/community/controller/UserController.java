package com.anyu.community.controller;

import com.anyu.community.annotation.RequiredLogin;
import com.anyu.community.entity.User;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CommunityUtil;
import com.anyu.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
@Validated
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    private final String PREFIX = "site/";
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;


    /**
     * 请求设置
     *
     * @return
     */
    @RequiredLogin
    @GetMapping("/setting")
    public String getSetting() {
        return PREFIX + "setting";
    }

    /**
     * 上传头像
     *
     * @param model
     * @param headImage
     * @return
     */
    @RequiredLogin
    @PostMapping("/upload")
    public String updateUserHeader(Model model, MultipartFile headImage) {
        if (!headImage.isEmpty()) {
            String filename = headImage.getOriginalFilename();
            //得到文件名后缀
            String suffix = filename.substring(filename.lastIndexOf("."));
            if (StringUtils.isBlank(suffix)) {
                model.addAttribute("headImageMsg", "文件格式不正确");
                return PREFIX + "setting";
            }
            //生成随机文件名
            filename = CommunityUtil.generateUUID() + suffix;
            //文件存放路径
            File file = new File(uploadPath + "/" + filename);
            try {
                //转存文件
                headImage.transferTo(file);
            } catch (IOException e) {
                logger.error("上传用户头像文件失败" + e.getMessage());
                throw new RuntimeException("上传用户头像文件失败", e);
            }
            //生成web访问路径URL
            //localhost:8080/community/user/header/***.png
            //http://localhost:8080/community/user/header/d592340424ca4bf7bc113a44baa50d55.png
            String headerUrl = domain + contextPath + "/user/header/" + filename;
            //更新头像
            User user = hostHolder.getUser();
            userService.updateUserHeaderUrl(user.getId(), headerUrl);
            return "redirect:/index";
        }
        model.addAttribute("headImageMsg", "请选择上传头像");
        return PREFIX + "setting";
    }

    /**
     * 访问头像
     *
     * @param filename
     * @param response
     */
    @GetMapping("/header/{filename}")
    public void getHeaderUrl(@PathVariable("filename") String filename, HttpServletResponse response) {
        //文件名后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //服务器存放路径
        filename = uploadPath + "/" + filename;
        //响应图片
        response.setContentType("image/" + suffix);
        try (
                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream inputStream = new FileInputStream(filename)
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
            throw new RuntimeException("读取头像失败", e);
        }
    }

    /**
     * 更新密码
     *
     * @param model
     * @param ticket
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @PostMapping("/update")
    @RequiredLogin
    public String updateUserPassword(Model model, @CookieValue("ticket") String ticket, @Length(min = 6, max = 15, message = "{user.password.length}") String oldPassword, @Length(min = 6, max = 15, message = "{user.password.length}") String newPassword) {
        if (userService.updateUserPassword(oldPassword, newPassword)) {
            userService.logout(ticket);
            return "redirect:/login";
        }
        model.addAttribute("passwordMsg", "输入密码错误");
        model.addAttribute("oldPassword", oldPassword);
        model.addAttribute("newPassword", newPassword);
        return PREFIX + "setting";
    }

}
