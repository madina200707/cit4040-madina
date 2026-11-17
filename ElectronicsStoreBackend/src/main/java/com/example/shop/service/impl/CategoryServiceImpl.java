package com.example.shop.service.impl;

import com.example.shop.dto.request.CreateCategoryRequest;
import com.example.shop.dto.request.ReorderCategoriesRequest;
import com.example.shop.dto.request.UpdateCategoryRequest;
import com.example.shop.dto.response.CategoryResponse;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.ConflictException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.CatalogMapper;
import com.example.shop.model.entity.Category;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.service.CategoryService;
import com.example.shop.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CatalogMapper mapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               CatalogMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getTree() {
        Map<Long, List<Category>> children = childrenByParent();
        return children.getOrDefault(null, List.of()).stream()
                .map(root -> toTree(root, children))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return toTree(findActive(id), childrenByParent());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getBySlug(String slug) {
        Category category = categoryRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("No category with slug " + slug));
        return toTree(category, childrenByParent());
    }

    @Override
    @Transactional(readOnly = true)
    public void requireActive(Long id) {
        findActive(id);
    }

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.name().trim());
        category.setSlug(uniqueSlug(request.name()));
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        category.setIsActive(true);
        if (request.parentCategoryId() != null) {
            category.setParentCategory(findActive(request.parentCategoryId()));
        }
        Category saved = categoryRepository.save(category);
        return mapper.toResponse(saved, List.of());
    }

    @Override
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Category category = findAny(id);
        if (request.name() != null) category.setName(request.name().trim());
        if (request.description() != null) category.setDescription(request.description());
        if (request.imageUrl() != null) category.setImageUrl(request.imageUrl());
        if (request.sortOrder() != null) category.setSortOrder(request.sortOrder());
        if (Boolean.TRUE.equals(request.moveToRoot())) {
            category.setParentCategory(null);
        } else if (request.parentCategoryId() != null) {
            Category parent = findActive(request.parentCategoryId());
            if (isSelfOrDescendant(category, parent)) {
                throw new BadRequestException(
                        "A category cannot be moved under itself or one of its subcategories");
            }
            category.setParentCategory(parent);
        }
        if (request.isActive() != null) {
            if (!request.isActive()) {
                checkCanDeactivate(category);
            }
            category.setIsActive(request.isActive());
        }
        return toTree(category, childrenByParent());
    }

    @Override
    public void delete(Long id) {
        Category category = findAny(id);
        checkCanDeactivate(category);
        category.setIsActive(false);
    }

    @Override
    public List<CategoryResponse> reorder(ReorderCategoriesRequest request) {
        for (ReorderCategoriesRequest.Item item : request.items()) {
            findAny(item.id()).setSortOrder(item.sortOrder());
        }
        categoryRepository.flush();
        return getTree();
    }

    private Category findActive(Long id) {
        return categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No category with id " + id));
    }

    private Category findAny(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No category with id " + id));
    }

    private void checkCanDeactivate(Category category) {
        if (categoryRepository.existsByParentCategoryIdAndIsActiveTrue(category.getId())) {
            throw new ConflictException("Category still has active subcategories");
        }
        if (productRepository.existsByCategoryIdAndIsActiveTrue(category.getId())) {
            throw new ConflictException("Category still has active products");
        }
    }

    private Map<Long, List<Category>> childrenByParent() {
        Map<Long, List<Category>> result = new HashMap<>();
        for (Category category : categoryRepository.findByIsActiveTrueOrderBySortOrderAscNameAsc()) {
            Long parentId = category.getParentCategory() == null
                    ? null : category.getParentCategory().getId();
            result.computeIfAbsent(parentId, key -> new ArrayList<>()).add(category);
        }
        return result;
    }

    private CategoryResponse toTree(Category category, Map<Long, List<Category>> children) {
        List<CategoryResponse> childResponses = children
                .getOrDefault(category.getId(), List.of()).stream()
                .map(child -> toTree(child, children))
                .toList();
        return mapper.toResponse(category, childResponses);
    }

    private boolean isSelfOrDescendant(Category category, Category candidateParent) {
        Category current = candidateParent;
        Set<Long> seen = new HashSet<>();
        while (current != null && seen.add(current.getId())) {
            if (current.getId().equals(category.getId())) {
                return true;
            }
            current = current.getParentCategory();
        }
        return false;
    }

    private String uniqueSlug(String name) {
        String base = SlugUtil.toSlug(name);
        String slug = base;
        int counter = 2;
        while (categoryRepository.existsBySlug(slug)) {
            slug = base + "-" + counter++;
        }
        return slug;
    }
}
