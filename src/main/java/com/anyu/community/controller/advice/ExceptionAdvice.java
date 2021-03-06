package com.anyu.community.controller.advice;

import com.anyu.community.utils.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 统一异常处理
 */
//@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //记录异常信息
        logger.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        //将异常信息呈现给客户端
        String typeWithRequest = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(typeWithRequest)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器发生异常"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
