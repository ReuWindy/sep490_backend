package com.fpt.sep490.controller;

import com.fpt.sep490.dto.NewDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.News;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.NewService;

import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/news")
public class NewController {

    private final NewService newService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;

    public NewController(NewService newService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService) {
        this.newService = newService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
    }

    @GetMapping
    public ResponseEntity<?> getNews() {
        List<News> list = newService.getAllNews();
        if(!list.isEmpty()) {
            return ResponseEntity.ok(list);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNew(HttpServletRequest request, @RequestBody NewDto news) {
        News createdNew = newService.createNew(news);
        String token = jwtTokenManager.resolveToken(request);
        String username = jwtTokenManager.getUsernameFromToken(token);
        userActivityService.logAndNotifyAdmin(username, "CREATE_NEW", news.getName());
        if(createdNew != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNew);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
