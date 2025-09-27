package com.spring.human.resource.server.payload.permission;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PermissionRequest {
    @NotBlank
    private String name;

    @NonNull
    private String description;
}
