package com.fpt.sep490.utils;

import java.util.Random;

public class RandomInventoryCodeGenerator {
    private static final String Inventory_PREFIX = "INVENTORY";

    public static String generateInventoryCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(9999);
        long timestamp = System.currentTimeMillis();

        return String.format("%s-%04d-%d", Inventory_PREFIX, randomNumber, timestamp);
    }
}
