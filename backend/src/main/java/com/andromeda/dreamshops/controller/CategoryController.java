package com.andromeda.dreamshops.controller;


import com.andromeda.dreamshops.dto.CategoryDto;
import com.andromeda.dreamshops.exceptions.AlreadyExistsException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.Category;
import com.andromeda.dreamshops.response.ApiResponse;
import com.andromeda.dreamshops.service.category.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    private final ICategoryService categoryService;

    // Get all categories
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            List<CategoryDto> categoryDtos = categoryService.convertToDto(categories);
            return ResponseEntity.ok(new ApiResponse("Found!", categoryDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error:", INTERNAL_SERVER_ERROR));
        }
    }

    // Add a new category
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category name) {
        try {
            Category savedCategory = categoryService.addCategory(name);
            CategoryDto categoryDto = categoryService.convertToDto(savedCategory);
            return ResponseEntity.ok(new ApiResponse("Category added successfully!", categoryDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Get category by id
    @GetMapping("/category/{id}/category")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            CategoryDto categoryDto = categoryService.convertToDto(category);
            return ResponseEntity.ok(new ApiResponse("Category found!", categoryDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Get category by name
    @GetMapping("/category/{name}/category")
    public ResponseEntity<ApiResponse> getCategoryByName(@PathVariable String name) {
        try {
            Category category = categoryService.getCategoryByName(name);
            CategoryDto categoryDto = categoryService.convertToDto(category);
            return ResponseEntity.ok(new ApiResponse("Category found!", categoryDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Delete category by id
    @DeleteMapping("/category/{id}/delete")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok(new ApiResponse("Category deleted successfully!", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Update category by id
    @PutMapping("/category/{id}/update")
    public ResponseEntity<ApiResponse> updateCategory(@RequestBody Category category, @PathVariable Long id) {
        try {
            Category updatedCategory = categoryService.updateCategory(category, id);
            CategoryDto categoryDto = categoryService.convertToDto(updatedCategory);
            return ResponseEntity.ok(new ApiResponse("Category updated successfully!", categoryDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Get direct sub-categories by parent id
    @GetMapping("/subcategory/parent/{parentId}/categories")
    public ResponseEntity<ApiResponse> getSubCategoriesByParentId(@PathVariable Long parentId) {
        try {
            List<Category> subCategories = categoryService.subCategoriesByParentId(parentId);
            List<CategoryDto> categoryDtos = categoryService.convertToDto(subCategories);
            return !categoryDtos.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Sub-categories found!", categoryDtos)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No sub-categories found for parent id: " + parentId, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch(Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }

    // Get direct sub-categories by parent name
    @GetMapping("/subcategory/by/parent-name")
    public ResponseEntity<ApiResponse> getSubCategoriesByParentName(@RequestParam String parentName) {
        try {
            List<Category> subCategories = categoryService.subCategoriesByParentName(parentName);
            List<CategoryDto> categoryDtos = categoryService.convertToDto(subCategories);
            return !categoryDtos.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Sub-categories found!", categoryDtos)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No sub-categories found for parent name: " + parentName, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }

    // get all sub categories under a parent category recursively
    @GetMapping("/subcategory/all/parent/{parentId}/categories")
    public ResponseEntity<ApiResponse> getAllSubCategories(@PathVariable Long parentId){
        try {
            List<Category> subCategories = categoryService.getAllSubCategoriesByParentId(parentId);
            List<CategoryDto> categoryDtos = categoryService.convertToDto(subCategories);
            return !categoryDtos.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("All sub-categories found!", categoryDtos)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No sub-categories found for parent id: " + parentId, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch(Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }

    // get all sub categories under a parent category recursively by parent name
    @GetMapping("/subcategory/all/parent-name")
    public ResponseEntity<ApiResponse> getAllSubCategoriesByParentName(@RequestParam String parentName) {
        try {
            List<Category> subCategories = categoryService.getAllSubCategoriesByParentName(parentName);
            List<CategoryDto> categoryDtos = categoryService.convertToDto(subCategories);
            return !categoryDtos.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("All sub-categories found!", categoryDtos)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No sub-categories found for parent name: " + parentName, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }

    // get category path by category id
    @GetMapping("/category/{id}/path")
    public ResponseEntity<ApiResponse> getCategoryPathById(@PathVariable Long id) {
        try {
            List<String> categoryPath = categoryService.getCategoryPathById(id);
            return !categoryPath.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Category path found!", categoryPath)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No category path found for id " + id, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }


    // get all top level categories
    @GetMapping("/category/parentCategory")
    public ResponseEntity<ApiResponse> getAllTopLevelCategories(){
        try{
            List<Category> parentCategories = categoryService.getTopLevelCategories();
            List<CategoryDto> categoryDtos = categoryService.convertToDto(parentCategories);
            return !categoryDtos.isEmpty()?
                    ResponseEntity.ok(new ApiResponse("Top Level categories found : ", categoryDtos)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No top level categories found !!", null));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }

    // get parent categories
    @GetMapping("/category/{categoryId}/parent/all")
    public ResponseEntity<ApiResponse> getAllParentCategories(@PathVariable Long categoryId){
        try {
            List<Category> parentCategories = categoryService.getParentCategories(categoryId);
            List<CategoryDto> convertedCats = categoryService.convertToDto(parentCategories);
            return !convertedCats.isEmpty()?
                    ResponseEntity.ok(new ApiResponse("Parent categories found: ", convertedCats)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No parent categories found !!", null));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }

    }


}
