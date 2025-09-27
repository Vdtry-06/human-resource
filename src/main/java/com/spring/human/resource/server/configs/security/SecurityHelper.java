package com.spring.human.resource.server.configs.security;

import com.spring.human.lib.exceptions.UnAuthorizationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/*
    Lấy thông tin user hiện tại theo username từ Spring Security Context
    Nếu user chưa đăng nhập (anonymous) thì sẽ ném ra exception
    Authentication chứa:
        principal: thông tin user (thường là object UserDetail)
        authorities: quyền user (ROLE_USER, ROLE, ADMIN,...)
        credentials: thông tin xác thực (thường là null sau khi login)
*/
@Log4j2
@Component
@RequiredArgsConstructor
public class SecurityHelper {
    public String getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnAuthorizationException("User not found");
        } else {
            return ((UserDetails)authentication.getPrincipal()).getUsername();
        }
    }
}
