package com.spring.human.resource.server.entities;

import com.spring.human.lib.enumerated.SystemRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

// đối tượng lớp Role có thể chuyển thành 1 dãy byte
// Lưu trạng thái đối tượng
// builder đôi khi làm code rối khi ít field và đơn giản
@Table(name = "roles", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", unique = true, nullable = false)
    private int roleId;

    @Column(name = "role_name", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private SystemRole roleName;

    @Column(name = "description")
    private String description;

    // eager: tải dữ liệu ngay lập tức, nhưng tốn bộ nhớ, chậm khi truy vấn lớn
    // lazy: tăng hiệu suất, giảm tải dữ liệu không cần thiết, nhưng tải dữ liệu chậm
    // vì không chạy ngay lập tức mà chỉ khi gọi mới chạy : sử dụng thêm transaction
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;
}
