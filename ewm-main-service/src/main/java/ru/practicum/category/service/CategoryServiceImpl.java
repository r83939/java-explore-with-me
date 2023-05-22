package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public Category addCategory(Category category) throws ConflictException, InvalidParameterException {
        log.info("Call#CategoryServiceImpl#create# : category: " + category.getName());
        if (category.getName().isBlank() || category.getName().isEmpty()) {
            throw new InvalidParameterException("Название категории должно быть задано.");
        }
        if (categoryRepository.existsCategoryByName(category.getName())) {
            throw new ConflictException("Уже есть категория с именем: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories(Integer from, Integer size) {
        log.info("Call#CategoryServiceImpl#etAllWithPagination# : from: {}, size: {}", from, size);
        return categoryRepository.getAllWithPagination(from, size);
    }

    @Override
    public Category getCategory(Integer categoryId) throws EntityNotFoundException {
        log.info("Call#CategoryServiceImpl#get# : category: " + categoryId);
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Нет Category с id: " + categoryId);
        }
        return category.get();
    }

    @Override
    public Category updateCategory(Integer categoryId, Category updateCategory) throws EntityNotFoundException, ConflictException {
        log.info("Call#CategoryServiceImpl#update# : category: " + updateCategory.getName());
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Нет Category с id: " + categoryId);
        }
        if (categoryRepository.existsCategoryByName(updateCategory.getName()) && !category.get().getName().equals(updateCategory.getName())) {
            throw new ConflictException("Уже есть категория с именем: " + updateCategory.getName());
        }
        category.get().setName(updateCategory.getName());
        return categoryRepository.save(category.get());
    }

    @Override
    public void deleteCategory(Integer categoryId) throws EntityNotFoundException, ConflictException {
        log.info("Call#CategoryServiceImpl#delete# : CategoryId: " + categoryId);
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new EntityNotFoundException("Нет Category с id: " + categoryId);
        }
        if (eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new ConflictException("Нельзя удалить категорию с id: {}" + categoryId);
        }
    }
}