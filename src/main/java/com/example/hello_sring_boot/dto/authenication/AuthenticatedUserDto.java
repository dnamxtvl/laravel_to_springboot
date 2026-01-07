package com.example.hello_sring_boot.dto.authenication;

import com.example.hello_sring_boot.enums.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticatedUserDto {

    private String name;

    private String username;

    private String password;

    private UserType userType;
}

