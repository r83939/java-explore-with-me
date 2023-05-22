package ru.practicum.category.service;

import ru.practicum.category.model.Category;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import java.util.List;

public interface CategoryService {
    Category addCategory(Category category) throws ConflictException, InvalidParameterException;

    List<Category> getAllCategories(Integer from, Integer size);

    Category getCategory(Integer id) throws EntityNotFoundException;

    Category updateCategory(Integer id, Category category) throws EntityNotFoundException, ConflictException;

    void deleteCategory(Integer id) throws EntityNotFoundException, ConflictException;
}