package ru.practicum.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.category.mapper.CategoryMapper.categoryMapper;

@Service
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = categoryRepo.save(categoryMapper.toCategory(categoryDto));
        log.info("Call CategoryService# Category created with id {}", category.getId());
        return categoryMapper.toCategoryDto(category);
    }

    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) throws ConflictException, EntityNotFoundException {
        Optional<Category> category = categoryRepo.findById(catId);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }

        if (categoryDto.getName().equals(category.get().getName())) {
            throw new ConflictException("Same category name");
        }
        category.get().setName(categoryDto.getName());
        log.info("Category with id {} updated", catId);
        return categoryMapper.toCategoryDto(categoryRepo.save(category.get()));
    }

    public void deleteCategory(long catId) throws EntityNotFoundException {
        Optional<Category> category = categoryRepo.findById(catId);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Category not found");
        }
        categoryRepo.deleteById(catId);
        log.info("Category with id {} deleted", catId);
    }

    public List<CategoryDto> getAll(Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        log.info("Categories sent");
        return categoryRepo.findAll(pageable).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getById(long catId) throws EntityNotFoundException {
        Optional<Category> category = categoryRepo.findById(catId);
        if (category.isEmpty()) {
            log.warn("Category not found");
            throw new EntityNotFoundException("Category not found");
        }
        log.info("Category with id {} sent", catId);
        return categoryMapper.toCategoryDto(category.get());
    }
}
