package com.andromeda.dreamshops.repository;

import com.andromeda.dreamshops.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    boolean existsByName(String name);
    // Additional query methods can be defined here if needed
}
