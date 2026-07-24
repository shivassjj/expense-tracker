package com.panchenko.expense_tracker.category;

import com.panchenko.expense_tracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.name = :name AND (c.user = :user OR c.user IS NULL)")
    Optional<Category> findByNameForUser(
            @Param("name") String name,
            @Param("user") User user
    );

    @Query("SELECT c FROM Category c WHERE (c.user = :user OR c.user IS NULL) ORDER BY c.name")
    List<Category> findAllForUser(@Param("user") User user);
}
