package com.spring.human.resource.server.entity;

import com.spring.human.lib.enumerated.EmployeeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.LocalDate;

// đối tượng lớp Employee có thể chuyển thành 1 dãy byte
// Lưu trạng thái đối tượng
// dùng builder dễ đọc dễ mở rộng dùng trong dto giao tiếp API
@Table(name = "employees", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id", unique = true, nullable = false)
    private int employeeId;

    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    // khi entity cha bị xóa hibernate sẽ set giá trị khóa ngoại của entity con về NULL
    // thay vì xóa bản ghi con
    @ManyToOne
    @JoinColumn(name = "position_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Position positionId;

    // khi entity cha bị xóa hibernate sẽ set giá trị khóa ngoại của entity con về NULL
    // thay vì xóa bản ghi con
    @ManyToOne
    @JoinColumn(name = "department_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Department departmentId;
}
