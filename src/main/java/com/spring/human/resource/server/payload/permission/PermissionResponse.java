package com.spring.human.resource.server.payload.permission;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {
    private int id;
    private String name;
    private String description;
}
