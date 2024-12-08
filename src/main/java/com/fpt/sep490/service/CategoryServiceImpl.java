package com.fpt.sep490.service;


import com.fpt.sep490.Enum.StatusEnum;
import com.fpt.sep490.dto.TopCategoryDto;
import com.fpt.sep490.dto.TopCategoryResponseDTO;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.OrderDetail;
import com.fpt.sep490.repository.CategoryRepository;
import com.fpt.sep490.repository.OrderDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
        List<OrderDetail> completedOrderDetails = orderDetailRepository.findAll().stream()
                .filter(od -> od.getOrder().getStatus() == StatusEnum.COMPLETED || od.getOrder().getStatus() == StatusEnum.COMPLETE)
                .toList();

        List<TopCategoryDto> topCategories = completedOrderDetails.stream()
                .collect(Collectors.groupingBy(
                        od -> od.getProduct().getCategory().getName(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> new TopCategoryDto(
                                        list.get(0).getProduct().getCategory().getName(),
                                        list.stream().map(od -> od.getOrder().getId()).distinct().count(),
                                        list.stream().mapToLong(OrderDetail::getQuantity).sum()
                                )
                        )
                ))
                .values()
                .stream()
                .sorted(Comparator.comparingLong(TopCategoryDto::getTotalQuantity).reversed())
                .limit(limit)
                .toList();
        double totalAmount = completedOrderDetails.stream()
                .filter(od -> topCategories.stream()
                        .map(TopCategoryDto::getCategoryName)
                        .anyMatch(categoryName -> categoryName.equals(od.getProduct().getCategory().getName())))
                .mapToDouble(OrderDetail::getTotalPrice)
                .sum();
        return new TopCategoryResponseDTO(topCategories, totalAmount);
    }
}
