package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryServiceImpl;
import ru.practicum.category.model.Category;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.EntityNotFoundException;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryServiceImpl categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Category create(@RequestBody @Valid Category category) throws ConflictException {
        log.info("Received a request to create a new Category: {}", category);
        return categoryService.create(category);
    }

    @PatchMapping("/{id}")
    public Category update(@PathVariable Integer id, @RequestBody @Valid Category category) throws ConflictException, EntityNotFoundException {
        log.info("Received a request to update a category with id: {}, Category: {}", id, category);
        return categoryService.update(id, category);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) throws EntityNotFoundException, ConflictException {
        log.info("Received a request to delete a category with id: {}", id);
        categoryService.delete(id);
    }
}