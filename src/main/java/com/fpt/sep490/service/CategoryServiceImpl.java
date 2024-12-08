package com.fpt.sep490.service;


import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.TopCategoryDto;
import com.fpt.sep490.dto.TopCategoryResponseDTO;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.Order;
import com.fpt.sep490.model.OrderDetail;
import com.fpt.sep490.repository.CategoryRepository;
import com.fpt.sep490.repository.OrderDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final OrderDetailRepository orderDetailRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, OrderDetailRepository orderDetailRepository) {
        this.categoryRepository = categoryRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(int id) {
        Optional<Category> category = categoryRepository.findById((long) id);
        return category.orElse(null);
    }

    @Override
    public Category getCategoryByName(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        return category.orElse(null);
    }

    @Override
    public Category createCategory(Category category) {
        Category newCategory = new Category();
        if (category.getName().isBlank()) {
            throw new RuntimeException("Tên danh mục không được trống");
        }
        newCategory.setName(category.getName());
        newCategory.setDescription(category.getDescription());
        newCategory.setActive(true);
        categoryRepository.save(newCategory);
        return newCategory;
    }

    @Override
    public Category updateCategory(Category category) {
        Category existingCategory = categoryRepository.findById(category.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        if (existingCategory != null) {
            if (category.getName().isBlank()) {
                throw new RuntimeException("Tên danh mục không được trống");
            }
            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
            categoryRepository.save(existingCategory);
            return existingCategory;
        } else {
            return null;
        }
    }

    @Override
    public List<String> getAllCategoryNames() {
        return categoryRepository.findAll()
                .stream()
                .takeWhile(Category::getActive)
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Category> getCategoriesByFilter(String name, Boolean status, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Specification<Category> specification = CategorySpecification.hasNameAndStatus(name, status);
            return categoryRepository.findAll(specification, pageable);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Category enableCategory(Long id) {
        Category categoryToEnable = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        categoryToEnable.setActive(true);
        return categoryToEnable;
    }

    @Override
    public Category disableCategory(Long id) {
        Category categoryToDisable = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        categoryToDisable.setActive(false);
        return categoryToDisable;
    }

    @Override
    public TopCategoryResponseDTO getTopCategoriesWithTotalAmount(int limit) {
        // Step 1: Get all completed order details
        List<OrderDetail> completedOrderDetails = orderDetailRepository.findAll().stream()
                .filter(od -> od.getOrder() != null &&
                        (od.getOrder().getStatus() == StatusEnum.COMPLETED ||
                                od.getOrder().getStatus() == StatusEnum.COMPLETE))
                .filter(od -> od.getProduct() != null && od.getProduct().getCategory() != null)
                .toList();

        Map<String, TopCategoryDto> categoryStats = new HashMap<>();
        for (OrderDetail orderDetail : completedOrderDetails) {
            String categoryName = orderDetail.getProduct().getCategory().getName();
            TopCategoryDto categoryDto = categoryStats.computeIfAbsent(categoryName,
                    name -> new TopCategoryDto(name, 0, 0));

            // Update totals for this category
            categoryDto.setTotalOrders(categoryDto.getTotalOrders() + 1);
            categoryDto.setTotalQuantity(categoryDto.getTotalQuantity() + orderDetail.getQuantity());
        }

        // Step 3: Sort categories by total quantity and limit to top N
        List<TopCategoryDto> topCategories = categoryStats.values().stream()
                .sorted(Comparator.comparingLong(TopCategoryDto::getTotalQuantity).reversed())
                .limit(limit)
                .toList();

        // Step 4: Calculate total amount for top categories
        Set<String> topCategoryNames = topCategories.stream()
                .map(TopCategoryDto::getCategoryName)
                .collect(Collectors.toSet());

        double totalAmount = completedOrderDetails.stream()
                .filter(od -> topCategoryNames.contains(od.getProduct().getCategory().getName()))
                .mapToDouble(OrderDetail::getTotalPrice)
                .sum();

        // Step 5: Return the response
        return new TopCategoryResponseDTO(topCategories, totalAmount);
    }
}