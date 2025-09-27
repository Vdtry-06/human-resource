package com.spring.human.resource.server.payload.employee;


import com.spring.human.lib.enumerated.EmployeeStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private LocalDate dateOfBirth;

    private String phoneNumber;

    private String address;

    private LocalDate hireDate;

    private Double salary;

    private EmployeeStatus status;

    private int userId;

    private int positionId;

    private int departmentId;
}
