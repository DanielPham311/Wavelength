package com.app.wavelength.common.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> content,
        long total,
        int limit,
        int offset
) {}
