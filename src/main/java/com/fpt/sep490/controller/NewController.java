package com.fpt.sep490.controller;

import com.fpt.sep490.dto.NewDto;
import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.News;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.NewService;

import com.fpt.sep490.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @GetMapping("/all")
    public ResponseEntity<?> getNews() {
        List<News> list = newService.getAllNews();
        if(!list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FOUND).body(list);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNews(@PathVariable int id) {
        News news = newService.getNewById(id);
        if(news != null) {
            return ResponseEntity.status(HttpStatus.FOUND).body(news);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    @GetMapping("/")
    public ResponseEntity<PagedModel<EntityModel<News>>> getAllNews(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<News> pagedResourcesAssembler) {
        Page<News> newsPage = newService.getNewsByFilter(name, type, username, pageNumber, pageSize);
        PagedModel<EntityModel<News>> pagedModel = pagedResourcesAssembler.toModel(newsPage);
        return ResponseEntity.status(HttpStatus.FOUND).body(pagedModel);
    }

    @PutMapping("/disable/{id}")
    public ResponseEntity<?> disableNew(@PathVariable int id) {
        newService.disableNews(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/disable/{id}")
    public ResponseEntity<?> enableNew(@PathVariable int id) {
        newService.enableNews(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNew(@PathVariable int id, @RequestBody NewDto news) {
        newService.updateNew(id, news);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
