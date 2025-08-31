package com.spring.human.resource.server.payload.position;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionResponse {
    private int positionId;
    private String positionName;
}
