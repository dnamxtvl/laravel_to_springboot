package com.example.hello_sring_boot.dto.response;

import java.util.List;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserWithPermsResponse {
    private String id;
    private String email;
    private String password;
    private String name;
    private List<String> permissions;
}
