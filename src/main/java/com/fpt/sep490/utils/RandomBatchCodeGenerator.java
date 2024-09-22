package com.fpt.sep490.utils;

import java.util.Random;

public class RandomBatchCodeGenerator {
    private static final String BATCH_PREFIX = "BATCH";
    public static String generateBatchCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(9999); // Generates a random number between 0 and 9999
        long timestamp = System.currentTimeMillis(); // Current timestamp for uniqueness

        return String.format("%s-%04d-%d", BATCH_PREFIX, randomNumber, timestamp);
    }
}
