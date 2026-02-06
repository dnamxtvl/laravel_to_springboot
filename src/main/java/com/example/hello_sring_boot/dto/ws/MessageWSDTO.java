package com.example.hello_sring_boot.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MessageWSDTO {
    private String content = "Hello world from backend";
}
