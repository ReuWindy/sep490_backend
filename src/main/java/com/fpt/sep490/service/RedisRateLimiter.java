package com.fpt.sep490.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RedisRateLimiter {
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> rateLimiterScript;

    public RedisRateLimiter(StringRedisTemplate redisTemplate, RedisScript<Long> rateLimiterScript) {
        this.redisTemplate = redisTemplate;
        this.rateLimiterScript = rateLimiterScript;
    }

    /**
     * Kiểm tra xem client có vượt giới hạn request không.
     *
     * @param clientId          ID duy nhất của client (IP hoặc User ID)
     * @param endpoint          Endpoint cần giới hạn
     * @param limit             Số request tối đa
     * @param timeWindowSeconds Thời gian giới hạn (tính bằng giây)
     * @return true nếu được phép, false nếu vượt quá giới hạn
     */
    public boolean isAllowed(String clientId, String endpoint, int limit, int timeWindowSeconds) {
        String key = String.format("rate_limit:%s:%s", clientId, endpoint);

        try {
            // Chạy script Lua để kiểm tra và tăng số lần request
            Long requestCount = redisTemplate.execute(
                    rateLimiterScript,
                    Collections.singletonList(key), // Key của rate limit
                    String.valueOf(limit), // Giới hạn số request tối đa
                    String.valueOf(timeWindowSeconds) // Thời gian hết hạn (TTL) tính bằng giây
            );

            // Log giá trị trả về từ Redis để kiểm tra
            System.out.println("Request count: " + requestCount);

            // Kiểm tra nếu số lần request vượt quá giới hạn
            if (requestCount == null) {
                return false; // Trường hợp lỗi (redis trả về null)
            }

            // Nếu số lần request hiện tại vượt quá giới hạn, trả về false
            if(requestCount == 0) return false;
            return requestCount <= limit;
        } catch (Exception e) {
            // Logging lỗi hoặc hành động khác nếu cần
            System.err.println("Error checking rate limit for client " + clientId + ": " + e.getMessage());
            return false; // Trong trường hợp có lỗi xảy ra, coi như request không được phép
        }
    }
}
