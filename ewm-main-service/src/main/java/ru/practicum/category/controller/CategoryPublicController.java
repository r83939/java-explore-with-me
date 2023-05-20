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
    public List<Category> getAllWithPagination(
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(defaultValue = "0") @Positive Integer from) {
        log.info("Received a request to get Categories size {} from {} ", size, from);
        return categoryService.getAllCategories(size, from);
    }

    @GetMapping("/{catId}")
    public Category get(@PathVariable Integer catId) throws EntityNotFoundException {
        log.info("Received a request to get Category with id {} ", catId);
        return categoryService.get(catId);
    }
}