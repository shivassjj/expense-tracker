package com.panchenko.expense_tracker.category;

import com.panchenko.expense_tracker.category.exception.CategoryAlreadyExistsException;
import com.panchenko.expense_tracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(String name, User user) {
        String normalizedName = normalizeName(name);
        if (categoryRepository.findByNameForUser(normalizedName, user).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name '" + normalizedName + "' already exists.");
        }

        Category category = new Category();
        category.setName(normalizedName);
        category.setUser(user);
        return categoryRepository.save(category);
    }

    public List<Category> getAvailableCategories(User user) {
        return categoryRepository.findAllForUser(user);
    }

    private String normalizeName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Category name cannot be null.");
        }
        String trimmedName = name.trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }

        return Character.toUpperCase(trimmedName.charAt(0)) +
                trimmedName.substring(1).toLowerCase(Locale.ROOT);
    }
}
