package com.andromeda.dreamshops.service.category;

import com.andromeda.dreamshops.dto.CategoryDto;
import com.andromeda.dreamshops.exceptions.AlreadyExistsException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.Category;
import com.andromeda.dreamshops.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found!!"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found!!"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category addCategory(Category category) {
        if(categoryRepository.existsByName(category.getName())){
            throw new AlreadyExistsException("Category already exists!!");
        }
        Optional.ofNullable(category.getParentCategory()).ifPresent(parent -> {
            Category parentCat = categoryRepository.findByName(parent.getName()).orElseGet(
                () -> categoryRepository.save(new Category(parent.getName())
            ));
            category.setParentCategory(parentCat);
        });

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(getCategoryById(id))
                .map(currentCategory -> {
                    currentCategory.setName(category.getName());
                    Optional.ofNullable(category.getParentCategory()).ifPresent(parent -> {
                        Category parentCat = categoryRepository.findByName(parent.getName()).orElseGet(
                            () -> categoryRepository.save(new Category(parent.getName())));

                        currentCategory.setParentCategory(parentCat);
                    });
                    return categoryRepository.save(currentCategory);
                }).orElseThrow(()-> new ResourceNotFoundException("Category not found for update!!"));

    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(categoryRepository::delete, ()->{
                        throw new ResourceNotFoundException("Category not found for deletion!!");
                     });
    }

    // This method fetches sub-categories based on the parent category id
    // only direct sub-categories, not recursive
    @Override
    public List<Category> subCategoriesByParentId(Long parentCategoryId) {
        return categoryRepository.findCategoriesByParentCategory_Id(parentCategoryId);
    }

    // This method fetches sub-categories based on the parent category name
    // only direct sub-categories, not recursive
    @Override
    public List<Category> subCategoriesByParentName(String parentName) {
        return categoryRepository.findCategoriesByParentCategory_Name(parentName);
    }

    // This method checks if a category exists by name, if yes returns it
    // else creates a new category. It also resolves the parent category similarly.
    @Override
    public Category resolveCategory(Category category) {
        return categoryRepository.findByName(category.getName())
                .orElseGet(() -> {
                    Category newCategory = new Category(category.getName());

                    Optional.ofNullable(category.getParentCategory())
                            .ifPresent(parentCategory -> {
                                Category parentCat = categoryRepository.findByName(parentCategory.getName()).orElseGet(
                                    () -> categoryRepository.save(new Category(parentCategory.getName())
                                ));
                            newCategory.setParentCategory(parentCat);
                    });
                    return categoryRepository.save(newCategory);
                });
    }

    // a method that will return all sub-categories under a parent category recursively
    // e.g., if Electronics has sub-category Computers, and Computers has sub-categories Laptop and Desktop
    // then getAllSubCategoriesByParentId(ElectronicsId) should return Computers, Laptop, Desktop
    // how it works - it first fetches direct sub-categories of the given parent category
    // then for each sub-category, it calls itself recursively to fetch their sub-categories
    // for our case Electronics -> Computers
    // then it calls getAllSubCategoriesByParentId(ComputersId) which returns Laptop and Desktop
    // then getAllSubCategoriesByParentId(LaptopId) and getAllSubCategoriesByParentId(DesktopId) return empty lists
    // this is where the recursion ends
    // finally it aggregates all these results into a single list and returns
    // this is a depth-first traversal of the category tree
    @Override
    public List<Category> getAllSubCategoriesByParentId(Long parentCategoryId) {
        List<Category> result = new ArrayList<>();
        List<Category> directSubCategories = categoryRepository.findCategoriesByParentCategory_Id(parentCategoryId);
        for (Category subCategory : directSubCategories) {
            result.add(subCategory);
            result.addAll(getAllSubCategoriesByParentId(subCategory.getId()));
        }
        return result;
    }

    @Override
    public List<Category> getAllSubCategoriesByParentName(String parentName) {
        List<Category> result = new ArrayList<>();
        Category currentCategory = getCategoryByName(parentName);
        List<Category> directSubCategories = categoryRepository.findCategoriesByParentCategory_Id(currentCategory.getId());
        for (Category subCategory : directSubCategories) {
            result.add(subCategory);
            result.addAll(getAllSubCategoriesByParentId(subCategory.getId()));
        }
        return result;
    }

    // now a method that will return the full category path for a given category id
    // e.g., if we have a category Laptop under Computers under Electronics
    // then getCategoryPath(LaptopId) should return ["Electronics", "Computers", "Laptop"]
    // how it works - it starts from the given category and keeps traversing up to its parent
    // until it reaches a category with no parent (top-level category)
    // it collects the names of these categories in a list
    // finally it reverses the list to get the path from top-level to the given category
    @Override
    public List<String> getCategoryPathById(Long categoryId) {
        List<String> path = new ArrayList<>();
        Category currentCategory = getCategoryById(categoryId);
        while (currentCategory.getParentCategory() != null) {
            path.addFirst( currentCategory.getName() );
            currentCategory = currentCategory.getParentCategory(); // move to parent
        }
        path.addFirst(currentCategory.getName());
        return path;
    }

    // a method that will return list categories that are on top of it
    // for laptop it will return [Electronics, Computers]
    @Override
    public List<Category> getParentCategories(Long categoryId) {
        List<Category> parents = new ArrayList<>();
        Category currentCategory = getCategoryById(categoryId);
        while (currentCategory.getParentCategory() != null) {
            currentCategory = currentCategory.getParentCategory();
            parents.add(currentCategory);
        }
        return parents;
    }

    // a method that will return the top-level categories (categories with no parent)
    @Override
    public List<Category> getTopLevelCategories() {
        return categoryRepository.findCategoriesByParentCategoryIsNull();
    }

    @Override
    public CategoryDto convertToDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> convertToDto(List<Category> categoryList) {
        return categoryList.stream()
                .map(this::convertToDto)
                .toList();
    }

}
