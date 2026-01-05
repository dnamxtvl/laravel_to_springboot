package com.example.hello_sring_boot.enums;

import lombok.Getter;

@Getter
public enum Gender {
    FEMALE((byte) 0, "Nữ"),
    MALE((byte) 1, "Nam"),
    OTHER((byte) 2, "Khác");

    private final byte value;
    private final String description;

    Gender(byte value, String description) {
        this.value = value;
        this.description = description;
    }

    public static Gender fromValue(byte value) {
        for (Gender gender : Gender.values()) {
            if (gender.getValue() == value) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender value: " + value);
    }
}
