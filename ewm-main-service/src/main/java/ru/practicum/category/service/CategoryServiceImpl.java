package ru.practicum.category.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.service.EventRepository;

import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    @Override
    public Category create(Category category) {
        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Уже есть категория с данным именем");
        }
    }

    @Override
    public List<Category> getAllWithPagination(Integer from, Integer size) {

        return categoryRepository.getAllWithPagination(from, size);
    }

    @Override
    public Category get(Integer id) {
        try {
            categoryRepository.getReferenceById(id).getName();
            return categoryRepository.getReferenceById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category с запрошенным id не существует");
        }
    }

    @Override
    public Category update(Integer id, Category category) {
        try {
            Category curCategory = categoryRepository.getReferenceById(id);
            curCategory.setName(category.getName());
            return categoryRepository.save(curCategory);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Уже есть категория с данным именем");
        }
    }

    @Override
    public void delete(Integer id) {
        if (eventRepository.findAllByCategoryId(id).isEmpty()) {
            categoryRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category с запрошенным id не существует или есть связанные ивенты");
        }
    }
}