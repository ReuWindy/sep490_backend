package com.fpt.sep490.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PorterStaticalCalculator {
    private Date dates;
    private double mass;

    public PorterStaticalCalculator(Date dates) {
        this.dates = dates;
        this.mass = 0;
    }

    public static List<PorterStaticalCalculator> createYearlyStatisticalList(int year) {
        List<PorterStaticalCalculator> yearlyStats = new ArrayList<>();
        for (int month = 0; month < 12; month++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, 1);
            PorterStaticalCalculator stat = new PorterStaticalCalculator(calendar.getTime());
            yearlyStats.add(stat);
        }
        return yearlyStats;
    }
}
