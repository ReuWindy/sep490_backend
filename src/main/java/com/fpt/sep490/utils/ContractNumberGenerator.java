package com.fpt.sep490.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContractNumberGenerator {
    private static final String CONTRACT_PREFIX = "CONTRACT";

    public static String generateContractCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(9999);   // Generates a random number between 0 and 9999
        long timestamp = System.currentTimeMillis();   // Current timestamp for uniqueness

        String genCode = String.format("%s-%d-%d", CONTRACT_PREFIX, randomNumber, timestamp);

        // Regular expression pattern that the code must match
        String regex = "^[A-Za-z0-9+_.-]*$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(regex);

        // Now create matcher object.
        Matcher matcher = pattern.matcher(genCode);
        if (!matcher.matches()) {
            System.out.println("Generated code does not match the required pattern!");
            return null;
        }
        return genCode;
    }
}
