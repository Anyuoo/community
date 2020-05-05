package com.anyu.community.controller.interceptor;

import com.anyu.community.entity.User;
import com.anyu.community.service.MessageService;
import com.anyu.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User holder = hostHolder.getUser();
        if (holder != null && modelAndView != null) {
            //未读消息数
            int unreadMsgCount = messageService.countUnreadLetters(holder.getId(), null) + messageService.countUnreadNotices(holder.getId(), null);
            modelAndView.addObject("unreadMsgCount", unreadMsgCount);
        }
    }
}
