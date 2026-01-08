package com.example.hello_sring_boot.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    @JsonProperty("items")
    private List<T> items;

    @JsonProperty("currentPage")
    private int currentPage;

    @JsonProperty("lastPage")
    private int lastPage;

    @JsonProperty("perPage")
    private int perPage;

    @JsonProperty("total")
    private long total;
}
