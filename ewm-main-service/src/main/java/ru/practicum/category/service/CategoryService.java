package ru.practicum.category.service;

import ru.practicum.category.model.Category;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;

import java.util.List;

public interface CategoryService {
    Category create(Category category) throws ConflictException;

    List<Category> getAllCategories(Integer from, Integer size);

    Category get(Integer id) throws EntityNotFoundException;

    Category update(Integer id, Category category) throws EntityNotFoundException, ConflictException;

    void delete(Integer id) throws EntityNotFoundException, ConflictException;
}