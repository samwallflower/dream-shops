package com.andromeda.dreamshops.controller;


import com.andromeda.dreamshops.dto.ProductDto;
import com.andromeda.dreamshops.exceptions.AlreadyExistsException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.Product;
import com.andromeda.dreamshops.request.AddProductRequest;
import com.andromeda.dreamshops.request.ProductUpdateRequest;
import com.andromeda.dreamshops.response.ApiResponse;
import com.andromeda.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final IProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts(){
        List<Product> products = productService.getAllProducts();
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found", null))
                : ResponseEntity.ok(new ApiResponse("Products retrieved successfully", convertedProducts));
    }

    @GetMapping("product/{productId}/product")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("Product retrieved successfully", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product) {
        try {
            Product savedProduct = productService.addProduct(product);
            ProductDto productDto = productService.convertToDto(savedProduct);
            return ResponseEntity.ok(new ApiResponse("Product added successfully", productDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(
                    new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/product/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long productId, @RequestBody ProductUpdateRequest request) {
        try {
            Product updatedProduct = productService.updateProduct( request, productId);
            ProductDto productDto = productService.convertToDto(updatedProduct);
            return ResponseEntity.ok(new ApiResponse("Product updated successfully", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/product/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Product deleted successfully", productId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("products/by/brand-and-name")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(@RequestParam String brandName,
                                                                @RequestParam String productName) {
        try {
            List<Product> products = productService.getProductByBrandAndName(brandName, productName);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return  convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("No products found for the given brand and name", null))
                : ResponseEntity.ok(new ApiResponse("Products retrieved successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse( e.getMessage(), null));
        }
    }

    @GetMapping("products/by/category-and-brand")
    public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(@RequestParam String category,
                                                                      @RequestParam String brand) {
        try {
            List<Product> products = productService.getProductByCategoryAndBrand(category, brand);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse("No products found for the given category and brand", null))
                :ResponseEntity.ok(new ApiResponse("Products retrieved successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("products/{name}/products")
    public ResponseEntity<ApiResponse> getProductsByName(@PathVariable String name) {
        try {
            List<Product> products = productService.getProductsByName(name);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND)
                        .body(new ApiResponse("No products found for the given name", null))
                : ResponseEntity.ok(new ApiResponse("Products retrieved successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/by-brand")
    public ResponseEntity<ApiResponse> getProductsByBrand(@RequestParam String brand) {
        try {
            List<Product> products = productService.getProductsByBrand(brand);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND)
                        .body(new ApiResponse("No products found for the given brand", null))
                : ResponseEntity.ok(new ApiResponse("Products retrieved successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/{category}/all/products")
    public ResponseEntity<ApiResponse> getAllProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productService.getAllProductsByCategory(category);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND)
                        .body(new ApiResponse("No products found for the given category", null))
                : ResponseEntity.ok(new ApiResponse("Products retrieved successfully", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/count/by-brand/and-name")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@RequestParam String brandName,
                                                                      @RequestParam String productName) {
        try {
            var productCount = productService.countProductByBrandAndName( brandName, productName);
            return ResponseEntity.ok(new ApiResponse("Product count!!", productCount));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

}
