package com.spring.human.lib.enumerated;

public enum SystemRole {
    ADMIN,
    MANAGER,
    EMPLOYEE;
    public static SystemRole fromStringToEnum(String input) {
        if (input == null) {
            // ngoại lệ xảy ra khi argument không hợp lệ.
            throw new IllegalArgumentException("Input is not null");
        }
        for(SystemRole role : SystemRole.values()) {
            // so sánh 2 chuỗi không phân biệt hoa thường
            if (role.name().equalsIgnoreCase(input)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Enum match not found: " + input);
    }
}
