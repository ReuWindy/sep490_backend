package com.fpt.sep490.utils;

import java.util.Random;

public class RandomExpenseCodeGenerator {
    private static final String EXPENSE_PREFIX = "EXPENSE";

    public static String generateExpenseCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        long timestamp = System.currentTimeMillis();

        return String.format("%s-%04d-%d", EXPENSE_PREFIX, randomNumber, timestamp);
    }
}
