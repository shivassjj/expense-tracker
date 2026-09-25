package com.panchenko.expense_tracker.expense;

import com.panchenko.expense_tracker.category.Category;
import com.panchenko.expense_tracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Transactional
    public Expense createExpense(String description,
                                 BigDecimal amount,
                                 User user,
                                 Category category) {
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setDescription(description);
        expense.setUser(user);
        expense.setCategory(category);
        expense.setCreatedAt(OffsetDateTime.now());
        return expenseRepository.save(expense);
    }
}
