package com.example.hello_sring_boot.dto.response;

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

    @Builder.Default
    private String message = "Success";

    private Data<T> data;

    @lombok.Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data<T> {
        private List<T> items;
        private int currentPage;
        private int lastPage;
        private int perPage;
        private long total;
    }
}
