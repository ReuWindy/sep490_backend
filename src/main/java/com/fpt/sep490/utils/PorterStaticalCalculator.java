package com.fpt.sep490.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PorterStaticalCalculator {
    private Date dates;
    private double mass;

    public PorterStaticalCalculator(Date dates) {
        this.dates = dates;
        this.mass = 0;
    }
}
