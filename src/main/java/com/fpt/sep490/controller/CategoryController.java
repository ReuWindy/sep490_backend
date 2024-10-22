package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.security.jwt.JwtTokenManager;
import com.fpt.sep490.service.CategoryService;
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
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final JwtTokenManager jwtTokenManager;
    private final UserActivityService userActivityService;

    public CategoryController(CategoryService categoryService, JwtTokenManager jwtTokenManager, UserActivityService userActivityService) {
        this.categoryService = categoryService;
        this.jwtTokenManager = jwtTokenManager;
        this.userActivityService = userActivityService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (!categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(categories);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable int id) {
        Category category = categoryService.getCategoryById(id);
        if (category != null) {
            return ResponseEntity.status(HttpStatus.OK).body(category);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getCategoryByName(@PathVariable String name) {
        Category category = categoryService.getCategoryByName(name);
        if (category != null) {
            return ResponseEntity.status(HttpStatus.OK).body(category);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createCategory")
    public ResponseEntity<?> createCategory(HttpServletRequest request, @RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        String token = jwtTokenManager.resolveToken(request);
        String username = jwtTokenManager.getUsernameFromToken(token);
        userActivityService.logAndNotifyAdmin(username, "CREATE_CATEGORY", category.getName());
        if (createdCategory != null) {

            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/updateCategory")
    public ResponseEntity<?> updateCategory(HttpServletRequest request, @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(category);
        String token = jwtTokenManager.resolveToken(request);
        String username = jwtTokenManager.getUsernameFromToken(token);
        userActivityService.logAndNotifyAdmin(username, "UPDATE_CATEGORY", "Update category: "+ updatedCategory.getName() + " by " + username);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCategory);
    }

    @GetMapping("/")
    public ResponseEntity<PagedModel<EntityModel<Category>>> getCategoriesByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            PagedResourcesAssembler<Category> pagedResourcesAssembler
    ) {
        Page<Category> categories = categoryService.getCategoriesByFilter(name, pageNumber, pageSize);
        PagedModel<EntityModel<Category>> pagedModel = pagedResourcesAssembler.toModel(categories);
        return ResponseEntity.status(HttpStatus.OK).body(pagedModel);
    }
}