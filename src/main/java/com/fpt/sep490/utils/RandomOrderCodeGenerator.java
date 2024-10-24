package com.fpt.sep490.utils;

import java.util.Random;

public class RandomOrderCodeGenerator {
    private static final String ORDER_PREFIX = "ORDER";

    public static String generateOrderCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(9999);
        long timestamp = System.currentTimeMillis();

        return String.format("%s-%04d-%d", ORDER_PREFIX, randomNumber, timestamp);
    }
}
