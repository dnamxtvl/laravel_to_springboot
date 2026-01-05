package com.example.hello_sring_boot.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE((byte) 1, "Hoạt động"),
    INACTIVE((byte) 0, "Không hoạt động"),
    BANNED((byte) -1, "Bị cấm");

    private final byte value;
    private final String description;

    UserStatus(byte value, String description) {
        this.value = value;
        this.description = description;
    }

    public static UserStatus fromValue(byte value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status value: " + value);
    }
}