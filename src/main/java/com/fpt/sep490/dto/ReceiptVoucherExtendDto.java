package com.fpt.sep490.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Data
public class ReceiptVoucherExtendDto {
    private Long id;
    private int number;
    private String type;
}
