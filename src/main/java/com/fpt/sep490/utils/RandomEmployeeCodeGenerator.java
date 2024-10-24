package com.fpt.sep490.utils;

import java.util.Random;

public class RandomEmployeeCodeGenerator {
    private static final String EMPLOYEE_PREFIX = "NV";

    public static String generateEmployeeCode(){
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        long timestamp = System.currentTimeMillis();

        return String.format("%s-%04d-%d",EMPLOYEE_PREFIX,randomNumber,timestamp);
    }
}
