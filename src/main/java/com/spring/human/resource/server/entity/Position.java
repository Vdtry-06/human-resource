package com.spring.human.resource.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

// đối tượng lớp Position có thể chuyển thành 1 dãy byte
// Lưu trạng thái đối tượng
// dùng builder dễ đọc dễ mở rộng dùng trong dto giao tiếp API
@Table(name = "positions", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Position implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id" , unique = true, nullable = false)
    private int positionId;

    @Column(name = "position_name", nullable = false, length = 255)
    private String positionName;
}
