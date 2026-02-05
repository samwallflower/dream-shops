package com.andromeda.dreamshops.service.product;

import com.andromeda.dreamshops.dto.ProductDto;
import com.andromeda.dreamshops.model.Product;
import com.andromeda.dreamshops.request.AddProductRequest;
import com.andromeda.dreamshops.request.ProductUpdateRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product, Long shopId);

    Product getProductById(Long id);
    void deleteProductById(Long id, Long shopId);
    Product updateProduct(ProductUpdateRequest product, Long productId, Long shopId);
    List<Product> getAllProducts();
    List<Product> getAllProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductByBrandAndName(String brand, String name);
    List<Product> getProductByCategoryAndBrand(String category, String brand);
    Long countProductByBrandAndName(String brand, String name);


    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);

    List<Product> getAllProductsByParentCategory(String parentCategoryName);

    // shop related product methods
    // MacBook Pro 14 in shop with id 1
    Product getProductByShopIdAndProductId(Long shopId, Long productId);

    // All products in shop with id 1
    List<Product> getAllProductsByShopId(Long shopId);

    // All products in Rio Electronics
    List<Product> getAllProductsByShopName(String shopName);

    // All laptops in Rio Electronics
    List<Product> getAllProductsByShopAndCategory(String shopName, String categoryName);

    // All Apple products in Rio Electronics
    List<Product> getAllProductsByShopAndBrand(String shopName, String brand);

    // All Apple laptops in Rio Electronics
    List<Product> getAllProductsByShopBrandAndCategory(String shopName, String brand, String categoryName);

    // MacBook Pro 14 in Rio Electronics
    Product getProductByShopNameAndProductName(String shopName, String productName);

    // Counting all products in a shop
    Long countProductsByShopId(Long shopId);
}