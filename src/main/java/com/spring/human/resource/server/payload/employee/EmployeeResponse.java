package com.spring.human.resource.server.payload.employee;

import com.spring.human.lib.enumerated.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeResponse {
    private int id;

    private String firstName;

    private String lastName;

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
