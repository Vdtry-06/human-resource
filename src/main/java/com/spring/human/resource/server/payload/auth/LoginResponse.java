package com.spring.human.resource.server.payload.auth;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private int id;
    private String username;
    private String email;
    private TokenResponse token;
}
