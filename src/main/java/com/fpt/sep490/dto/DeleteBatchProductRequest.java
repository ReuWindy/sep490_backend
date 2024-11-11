package com.fpt.sep490.dto;

import lombok.Data;

import java.util.List;
@Data

public class DeleteBatchProductRequest {
    private List<Long> batProductId;
}
