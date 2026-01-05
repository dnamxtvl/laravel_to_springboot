package com.example.hello_sring_boot.dto.request;

import com.example.hello_sring_boot.enums.Gender;
import com.example.hello_sring_boot.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must be less than 255 characters")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private Gender gender;

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

    @Size(max = 255, message = "About me must be less than 255 characters")
    private String aboutMe;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "User type is required")
    private UserType typeUser;
}