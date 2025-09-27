package com.spring.human.resource.server.configs.security;

import com.spring.human.resource.server.entities.User;
import com.spring.human.resource.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userService.findByFields(Map.of("username", username));
        if (user != null) {
            final var authorizations = user.getRoleId().getPermissions()
                    .stream().map(permission -> new SimpleGrantedAuthority(permission.getPermissionName()))
                    .toList();

            return org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password(user.getPassword())
                    .authorities(authorizations)
                    .build();
        }
        return null;
    }
}
