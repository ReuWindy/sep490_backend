package com.fpt.sep490.utils;

import java.util.Random;

public class RandomProductCodeGenerator {

    private static final String PRODUCT_PREFIX = "PROD";

    public static String generateProductCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(9999);
        long timestamp = System.currentTimeMillis();

        return String.format("%s-%04d-%d", PRODUCT_PREFIX, randomNumber, timestamp);
    }
}
