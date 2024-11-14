package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.Supplier;
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

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable int id) {
        Category category = categoryService.getCategoryById(id);
        if (category != null) {
            return ResponseEntity.status(HttpStatus.OK).body(category);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("getByName/{name}")
    public ResponseEntity<?> getCategoryByName(@PathVariable String name) {
        Category category = categoryService.getCategoryByName(name);
        if (category != null) {
            return ResponseEntity.status(HttpStatus.OK).body(category);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllActiveSupplierNames() {
        List<String> resultList = categoryService.getAllCategoryNames();
        if(!resultList.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(resultList);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @PostMapping("/createCategory")
    public ResponseEntity<?> createCategory(HttpServletRequest request, @RequestBody Category category) {
        try{
            Category createdCategory = categoryService.createCategory(category);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "CREATE_CATEGORY", "Tạo danh mục: "+ createdCategory.getName() + " by " + username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        }catch (Exception e){
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/updateCategory")
    public ResponseEntity<?> updateCategory(HttpServletRequest request, @RequestBody Category category) {
        try{
            Category updatedCategory = categoryService.updateCategory(category);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "UPDATE_CATEGORY", "Update category: "+ updatedCategory.getName() + " by " + username);
            return ResponseEntity.status(HttpStatus.OK).body(updatedCategory);
        }catch (Exception e){
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/getByFilter")
    public ResponseEntity<PagedModel<EntityModel<Category>>> getCategoriesByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(required = false) Boolean status,
            PagedResourcesAssembler<Category> pagedResourcesAssembler
    ) {
        Page<Category> categories = categoryService.getCategoriesByFilter(name, status, pageNumber, pageSize);
        PagedModel<EntityModel<Category>> pagedModel = pagedResourcesAssembler.toModel(categories);
        return ResponseEntity.status(HttpStatus.OK).body(pagedModel);
    }

    @PostMapping("/disable/{id}")
    public ResponseEntity<?> disableCategory(HttpServletRequest request, @PathVariable long id) {
        try {
            Category category = categoryService.disableCategory(id);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "DISABLE_CATEGORY", "Ẩn danh mục "+ category.getName()+ " bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(category);
        } catch (Exception e){
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/enable/{id}")
    public ResponseEntity<?> enableCategory(HttpServletRequest request, @PathVariable long id) {
        try {
            Category category = categoryService.enableCategory(id);
            String token = jwtTokenManager.resolveToken(request);
            String username = jwtTokenManager.getUsernameFromToken(token);
            userActivityService.logAndNotifyAdmin(username, "ENABLE_CATEGORY", "Kích hoạt danh mục "+ category.getName()+ " bởi người dùng: " + username);
            return ResponseEntity.status(HttpStatus.OK).body(category);
        } catch (Exception e){
            final ApiExceptionResponse response = new ApiExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}