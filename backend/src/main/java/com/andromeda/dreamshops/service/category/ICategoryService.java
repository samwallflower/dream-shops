package com.andromeda.dreamshops.service.category;

import com.andromeda.dreamshops.dto.CategoryDto;
import com.andromeda.dreamshops.model.Category;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(Long id);
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    Category addCategory(Category category);
    Category updateCategory(Category category, Long id);
    void deleteCategoryById(Long id);

    List<Category> subCategoriesByParentId(Long parentId);
    List<Category> subCategoriesByParentName(String parentName);

    Category resolveCategory(Category category);

    // a method that will return all sub-categories under a parent category recursively
    // e.g., if Electronics has sub-category Computers, and Computers has sub-categories Laptop and Desktop
    // then getAllSubCategoriesByParentId(ElectronicsId) should return Computers, Laptop, Desktop
    List<Category> getAllSubCategoriesByParentId(Long parentCategoryId);

    List<Category> getAllSubCategoriesByParentName(String parentName);

    // now a method that will return the full category path for a given category id
    // e.g., if we have a category Laptop under Computers under Electronics
    // then getCategoryPath(LaptopId) should return ["Electronics", "Computers", "Laptop"]
    // how it works - it starts from the given category and keeps traversing up to its parent
    // until it reaches a category with no parent (top-level category)
    // it collects the names of these categories in a list
    // finally it reverses the list to get the path from top-level to the given category
    List<String> getCategoryPathById(Long categoryId);

    // a method that will return list categories that are on top of it
    // for laptop it will return [Electronics, Computers]
    List<Category> getParentCategories(Long categoryId);

    // a method that will return the top-level categories (categories with no parent)
    List<Category> getTopLevelCategories();

    CategoryDto convertToDto(Category category);

    List<CategoryDto> convertToDto(List<Category> categoryList);
}
