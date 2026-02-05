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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private final IProductService productService;

    //get all products
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts(){
        List<Product> products = productService.getAllProducts();
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found", null))
                : ResponseEntity.ok(new ApiResponse("Products retrieved successfully", convertedProducts));
    }

    //get product by id
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

    // add product to shop
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/shop/{shopId}/product/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product, @PathVariable Long shopId) {
        try {
            Product savedProduct = productService.addProduct(product, shopId);
            ProductDto productDto = productService.convertToDto(savedProduct);
            return ResponseEntity.ok(new ApiResponse("Product added successfully", productDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(
                    new ApiResponse(e.getMessage(), null));
        }
    }

    // update product
   // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/shop/{shopId}/product/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long productId, @RequestBody ProductUpdateRequest request, @PathVariable Long shopId) {
        try {
            Product updatedProduct = productService.updateProduct( request, productId, shopId);
            ProductDto productDto = productService.convertToDto(updatedProduct);
            return ResponseEntity.ok(new ApiResponse("Product updated successfully", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // delete product
   // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/shop/{shopId}/product/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId, @PathVariable Long shopId) {
        try {
            productService.deleteProductById(productId, shopId);
            return ResponseEntity.ok(new ApiResponse("Product deleted successfully", productId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // search products by brand and name
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

    // search products by category and brand
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

    // search products by name - iphone 17
    @GetMapping("products/by/name/products")
    public ResponseEntity<ApiResponse> getProductsByName(@RequestParam String name) {
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

    // search products by brand - Apple , Samsung , Dell , HP , Sony , LG , Asus , Acer
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

    // search products by category - laptops , phones , gadgets , accessories , peripherals , software , gaming , networking
    @GetMapping("/product/by/category/all/products")
    public ResponseEntity<ApiResponse> getAllProductsByCategory(@RequestParam String category) {
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

    // count products by brand and name - Apple iPhone 13
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

    // get all products under a parent category
    // ex - electronics , computers etc.
    @GetMapping("/product/by/parent-category/all")
    public ResponseEntity<ApiResponse> getAllProductsByParentCategory(@RequestParam String parentCategory){
        try {
            List<Product> products = productService.getAllProductsByParentCategory(parentCategory);
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return !convertedProducts.isEmpty()?
                    ResponseEntity.ok(new ApiResponse("Products found for " + parentCategory +" category: ", convertedProducts)):
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found for the "+parentCategory+" category!!", null));
        } catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Error: "+ e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }

    }

    // shop related product endpoints
    // product 1 in shop 2
    @GetMapping("/product/by/shop-and-product-id")
    public ResponseEntity<ApiResponse> getProductByShopIdAndProductId(@RequestParam Long shopId,
                                                                @RequestParam Long productId) {
        try {
            Product product = productService.getProductByShopIdAndProductId(shopId, productId);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("Product retrieved successfully", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // All products in shop id 2
    @GetMapping("/shops/{shopId}/products")
    public ResponseEntity<ApiResponse> getAllProductsByShopId(@PathVariable Long shopId) {
        List<Product> products = productService.getAllProductsByShopId(shopId);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found for shop id: " + shopId, null))
                : ResponseEntity.ok(new ApiResponse("Products for shop id: " + shopId + " retrieved successfully", convertedProducts));
    }

    //ALl products in Rio Electronics
    @GetMapping("/shops/shopName/products")
    public ResponseEntity<ApiResponse> getAllProductsByShopName(@RequestParam String shopName) {
        List<Product> products = productService.getAllProductsByShopName(shopName);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found for shop name: " + shopName, null))
                : ResponseEntity.ok(new ApiResponse("Products for shop name: " + shopName + " retrieved successfully", convertedProducts));
    }

    // All gadgets in Rio Electronics
    @GetMapping("/products/by/shop-and-category")
    public ResponseEntity<ApiResponse> getAllProductsByShopAndCategory(@RequestParam String shopName, @RequestParam String categoryName) {
        List<Product> products = productService.getAllProductsByShopAndCategory(shopName, categoryName);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found for shop: " + shopName + " and category: " + categoryName, null))
                : ResponseEntity.ok(new ApiResponse("Products for shop: " + shopName + " and category: " + categoryName + " retrieved successfully", convertedProducts));
    }

    // All Apple products in Rio Electronics (laptops , phones , gadgets etc)
    @GetMapping("/products/by/shop-and-brand")
    public ResponseEntity<ApiResponse> getAllProductsByShopAndBrand(@RequestParam String shopName, @RequestParam String brand) {
        List<Product> products = productService.getAllProductsByShopAndBrand(shopName, brand);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found for shop: " + shopName + " and brand: " + brand, null))
                : ResponseEntity.ok(new ApiResponse("Products for shop: " + shopName + " and brand: " + brand + " retrieved successfully", convertedProducts));
    }

    // All Apple phones in Rio Electronics
    @GetMapping("/products/by/shop-brand-and-category")
    public ResponseEntity<ApiResponse> getAllProductsByShopBrandAndCategory(@RequestParam String shopName, @RequestParam String brand, @RequestParam String categoryName) {
        List<Product> products = productService.getAllProductsByShopBrandAndCategory(shopName, brand, categoryName);
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return convertedProducts.isEmpty()
                ? ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found for shop: " + shopName + ", brand: " + brand + " and category: " + categoryName, null))
                : ResponseEntity.ok(new ApiResponse("Products for shop: " + shopName + ", brand: " + brand + " and category: " + categoryName + " retrieved successfully", convertedProducts));
    }

    // iPhone 13 in Rio Electronics
    @GetMapping("/product/by/shop-and-product-name")
    public ResponseEntity<ApiResponse> getProductByShopNameAndProductName(@RequestParam String shopName, @RequestParam String productName) {
        try {
            Product product = productService.getProductByShopNameAndProductName(shopName, productName);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("Product retrieved successfully", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/count/by/{shopId}")
    public ResponseEntity<ApiResponse> countProductsByShopId(@PathVariable Long shopId) {
        try {
            var productCount = productService.countProductsByShopId(shopId);
            return ResponseEntity.ok(new ApiResponse("Product count for shop id: " + shopId, productCount));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

}