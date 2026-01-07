package com.example.hello_sring_boot.dto.success;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SuccessResponse {
    private String message = "Success";
    private Object data;
}
