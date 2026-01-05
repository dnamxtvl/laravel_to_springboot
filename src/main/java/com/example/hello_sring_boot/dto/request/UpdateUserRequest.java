package com.example.hello_sring_boot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    @Size(max = 255, message = "About me must be less than 255 characters")
    private String aboutMe;

    private String avatar;
    private String backgroundProfile;
    private Integer fromCityId;
    private Integer currentCityId;
    private Integer relationshipStatus;

    @Min(1) @Max(31)
    private Integer dayOfBirth;

    @Min(1) @Max(12)
    private Integer monthOfBirth;

    @Min(1900) @Max(2100)
    private Integer yearOfBirth;
}