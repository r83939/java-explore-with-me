package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryServiceImpl;
import ru.practicum.category.model.Category;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.InvalidParameterException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryServiceImpl categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Category addCategory(@RequestBody @Valid Category category) throws ConflictException, InvalidParameterException {
        log.info("Call #CategoryAdminController#addCategory# category: {}", category);
        return categoryService.addCategory(category);
    }

    @PatchMapping("/{id}")
    public Category updateCategory(@Positive  @PathVariable Long id,
                                   @RequestBody @Valid Category category) throws ConflictException, EntityNotFoundException {
        log.info("Call #CategoryAdminController#updateCategory# categoryId: {}", id);
        return categoryService.updateCategory(id, category);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCategory(@Positive @PathVariable Long id) throws EntityNotFoundException, ConflictException {
        log.info("Call #CategoryAdminController#deleteCategory# categoryId: {}", id);
        categoryService.deleteCategory(id);
    }
}