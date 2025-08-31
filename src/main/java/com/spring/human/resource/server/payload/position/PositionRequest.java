package com.spring.human.resource.server.payload.position;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionRequest implements Serializable {
    @NotNull
    @JsonProperty("positionName")
    private String positionName;
}
