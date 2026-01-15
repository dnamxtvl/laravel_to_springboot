package com.example.hello_sring_boot.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DetailTodos {
    private Integer userId;
    private Integer id;
    private String title;
    private Boolean completed;
}
