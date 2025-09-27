package com.spring.human.resource.server.payload.role;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class RoleRequest {
    @NotBlank
    private String name;

    @NonNull
    private String description;
}
