package com.spring.human.resource.server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

// đối tượng lớp Department có thể chuyển thành 1 dãy byte
// Lưu trạng thái đối tượng
// dùng builder dễ đọc dễ mở rộng dùng trong dto giao tiếp API
@Table(name = "departments", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Department implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id" , unique = true, nullable = false)
    private int departmentId;

    @Column(name = "department_name", nullable = false, length = 255)
    private String departmentName;

    @Column(name = "desciption")
    private String desciption;
}
