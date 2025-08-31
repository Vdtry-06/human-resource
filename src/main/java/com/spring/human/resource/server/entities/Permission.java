package com.spring.human.resource.server.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

// đối tượng lớp Permission có thể chuyển thành 1 dãy byte
// Lưu trạng thái đối tượng
// builder đôi khi làm code rối khi ít field và đơn giản
@Table(name = "permissions", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Permission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permissionId", unique = true, nullable = false)
    private int permissionId;

    @Column(name = "permission_name", nullable = false, length = 255)
    private String permissionName;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;
}
