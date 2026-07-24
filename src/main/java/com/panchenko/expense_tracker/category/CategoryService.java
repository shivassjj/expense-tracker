package com.panchenko.expense_tracker.category;

import com.panchenko.expense_tracker.category.exception.CategoryAlreadyExistsException;
import com.panchenko.expense_tracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(String name, User user) {
        if (categoryRepository.findByNameForUser(name, user).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name '" + name + "' already exists.");
        }

        Category category = new Category();
        category.setName(name);
        category.setUser(user);
        return categoryRepository.save(category);
    }

    public List<Category> getAvailableCategories(User user) {
        return categoryRepository.findAllForUser(user);
    }
}
