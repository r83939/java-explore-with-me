package ru.practicum.category.service;

import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {
    Category create(Category category);

    List<Category> getAllWithPagination(Integer from, Integer size);

    Category get(Integer id);

    Category update(Integer id, Category category);

    void delete(Integer id);
}