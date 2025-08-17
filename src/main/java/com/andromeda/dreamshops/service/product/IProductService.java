package com.andromeda.dreamshops.service.product;

import com.andromeda.dreamshops.dto.ProductDto;
import com.andromeda.dreamshops.model.Product;
import com.andromeda.dreamshops.request.AddProductRequest;
import com.andromeda.dreamshops.request.ProductUpdateRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);

    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest product, Long productId);
    List<Product> getAllProducts();
    List<Product> getAllProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductByBrandAndName(String brand, String name);
    List<Product> getProductByCategoryAndBrand(String category, String brand);
    Long countProductByBrandAndName(String brand, String name);


    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);
}