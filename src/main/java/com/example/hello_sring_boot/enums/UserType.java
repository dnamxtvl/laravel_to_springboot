package com.example.hello_sring_boot.enums;

import lombok.Getter;

@Getter
public enum UserType {
    STUDENT((byte) 1, "Học sinh"),
    PARENT((byte) 2, "Phụ huynh"),
    SCHOOL((byte) 3, "Nhà trường"),
    ADMIN((byte) 4, "Admin");

    private final byte value;
    private final String description;

    UserType(byte value, String description) {
        this.value = value;
        this.description = description;
    }

    public static UserType fromValue(byte value) {
        for (UserType type : UserType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown user type value: " + value);
    }
}