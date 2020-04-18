package com.anyu.community.controller.interceptor;

import com.anyu.community.entity.LoginTicket;
import com.anyu.community.entity.User;
import com.anyu.community.service.UserService;
import com.anyu.community.utils.CookieUtil;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 登录状态拦截器
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //根据cookie获取登录凭证
        String ticket = CookieUtil.getCookieValue(request, "ticket");
        if (ticket != null) {
            //根据登录凭证得到LoginTicket对象
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //登录信息是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //查询登录用户信息
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求持有用户信息
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null)
            modelAndView.addObject("loginUser", user);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.deleteUser();
    }
}
