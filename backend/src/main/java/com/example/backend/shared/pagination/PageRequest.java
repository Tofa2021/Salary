package com.example.backend.shared.pagination;

public record PageRequest(int page, int size) {
    public PageRequest {
        if (page < 0) {
            throw new IllegalArgumentException("page >= 0");
        }
        
        if (size <= 0) {
            throw new IllegalArgumentException("size > 0");
        }
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }

    public int offset() {
        return page * size;
    }
}
