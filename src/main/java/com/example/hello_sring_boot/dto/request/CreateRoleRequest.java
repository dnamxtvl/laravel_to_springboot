package com.example.hello_sring_boot.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class CreateRoleRequest {
    @NotEmpty(message = "role.create.name_required")
    @Size(min = 3, max = 50, message = "role.create.name_size")
    private String name;

    @NotEmpty(message = "role.create.permissions_required")
    private List<
            @NotNull(message = "permission.id.null")
            @Positive(message = "permission.id.positive") Integer> permission_ids;
}
