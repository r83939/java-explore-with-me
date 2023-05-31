package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryServiceImpl;
import ru.practicum.category.model.Category;
import ru.practicum.exception.EntityNotFoundException;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {

    private final CategoryServiceImpl categoryService;

    @GetMapping
    public List<Category> getAllCategoriesWithPagination(
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(defaultValue = "0") @Positive Integer from) {
        log.info("Call #CategoryPublicControlle#getAllCategoriesWithPagination# size {} from {} ", size, from);
        return categoryService.getAllCategories(size, from);
    }

    @GetMapping("/{catId}")
    public Category getCategory(@PathVariable Long catId) throws EntityNotFoundException {
        log.info("Call #CategoryPublicControlle# categoryId {}", catId);
        return categoryService.getCategory(catId);
    }
}