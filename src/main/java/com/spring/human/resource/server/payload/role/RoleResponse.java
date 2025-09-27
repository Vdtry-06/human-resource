package com.spring.human.resource.server.payload.role;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    private int id;
    private String name;
    private String description;
}
