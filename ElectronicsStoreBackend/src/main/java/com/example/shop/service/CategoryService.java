package com.example.shop.service;

import com.example.shop.dto.request.CreateCategoryRequest;
import com.example.shop.dto.request.ReorderCategoriesRequest;
import com.example.shop.dto.request.UpdateCategoryRequest;
import com.example.shop.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getTree();

    CategoryResponse getById(Long id);

    CategoryResponse getBySlug(String slug);

    void requireActive(Long id);

    CategoryResponse create(CreateCategoryRequest request);

    CategoryResponse update(Long id, UpdateCategoryRequest request);

    void delete(Long id);

    List<CategoryResponse> reorder(ReorderCategoriesRequest request);
}
