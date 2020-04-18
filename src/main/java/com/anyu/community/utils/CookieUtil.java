package com.anyu.community.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 查询cookie值
 */
public class CookieUtil {
    public static String getCookieValue(HttpServletRequest request, String key) {
        if (request == null || key == null)
            throw new IllegalArgumentException("查询cookie失败，参数为空");
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key))
                return cookie.getValue();
        }
        return null;
    }
}
