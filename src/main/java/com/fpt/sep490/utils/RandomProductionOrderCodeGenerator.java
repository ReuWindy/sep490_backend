package com.fpt.sep490.utils;

import java.util.Random;

public class RandomProductionOrderCodeGenerator {
    private static final String PRODUCTION_ORDER_PREFIX = "PRODUCE";

    public static String generateOrderCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(9999);
        long timestamp = System.currentTimeMillis();

        return String.format("%s-%04d-%d", PRODUCTION_ORDER_PREFIX, randomNumber, timestamp);
    }
}
