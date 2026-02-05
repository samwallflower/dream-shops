package com.andromeda.dreamshops.repository;

import com.andromeda.dreamshops.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    boolean existsByName(String name);


    List<Category> findCategoriesByParentCategory_Id(Long parentCategoryId);
    List<Category> findCategoriesByParentCategory_Name(String parentCategoryName);

    List<Category> findCategoriesByParentCategoryIsNull();
}
