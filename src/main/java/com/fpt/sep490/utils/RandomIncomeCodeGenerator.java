package com.fpt.sep490.utils;

import java.util.Random;

public class RandomIncomeCodeGenerator {
    private static final String INCOME_PREFIX = "INCOME";

    public static String generateIncomeCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        long timestamp = System.currentTimeMillis();

        return String.format("%s-%04d-%d", INCOME_PREFIX, randomNumber, timestamp);
    }
}

