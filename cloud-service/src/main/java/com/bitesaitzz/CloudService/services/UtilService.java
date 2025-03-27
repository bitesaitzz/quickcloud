package com.bitesaitzz.CloudService.services;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UtilService {
    private static final String USER_ID_COOKIE_NAME = "userId";

    public UUID checkAndGenerateUserId(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        String userId = null;

        // Ищем cookie с ID пользователя
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (USER_ID_COOKIE_NAME.equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }


        if (userId == null) {
            userId = UUID.randomUUID().toString();
            Cookie userIdCookie = new Cookie(USER_ID_COOKIE_NAME, userId);
            userIdCookie.setMaxAge(60 * 60 * 24 * 365); //1 year
            userIdCookie.setPath("/");
            response.addCookie(userIdCookie);
        }

        return UUID.fromString(userId);
    }
}
