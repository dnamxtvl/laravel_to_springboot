package com.example.hello_sring_boot.mapper;

import com.example.hello_sring_boot.dto.response.PaginatedResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public class PaginationMapper {

    /**
     * Convert Spring Page to custom PaginatedResponse with mapper
     */
    public static <T, R> PaginatedResponse<R> toPaginatedResponse(
            Page<T> page,
            Function<T, R> mapper) {

        List<R> items = page.getContent().stream()
                .map(mapper)
                .toList();

        return buildPaginatedResponse(page, items);
    }

    /**
     * Convert Spring Page to custom PaginatedResponse without mapper
     */
    public static <T> PaginatedResponse<T> toPaginatedResponse(Page<T> page) {
        return buildPaginatedResponse(page, page.getContent());
    }

    private static <T> PaginatedResponse<T> buildPaginatedResponse(Page<?> page, List<T> items) {
        PaginatedResponse.Data<T> data = PaginatedResponse.Data.<T>builder()
                .items(items)
                .currentPage(page.getNumber() + 1) // Spring: 0-based → 1-based
                .lastPage(page.getTotalPages())
                .perPage(page.getSize())
                .total(page.getTotalElements())
                .build();

        return PaginatedResponse.<T>builder()
                .data(data)
                .build();
    }
}
