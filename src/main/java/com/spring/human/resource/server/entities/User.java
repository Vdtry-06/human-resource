package com.spring.human.resource.server.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// đối tượng lớp User có thể chuyển thành 1 dãy byte
// Lưu trạng thái đối tượng
// builder đôi khi làm code rối khi ít field và đơn giản
@Table(name = "users", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private int userId;

    @Column(name = "username", unique = true, nullable = false, length = 255)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    // eager: tải dữ liệu ngay lập tức, nhưng tốn bộ nhớ, chậm khi truy vấn lớn
    // lazy: tăng hiệu suất, giảm tải dữ liệu không cần thiết, nhưng tải dữ liệu chậm
    // vì không chạy ngay lập tức mà chỉ khi gọi mới chạy : sử dụng thêm transaction
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role roleId;

    // lan truyền entity cha sang entity con
    // 1 User có nhiều employee
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId")
    private List<Employee> employees = new ArrayList<>();

}
