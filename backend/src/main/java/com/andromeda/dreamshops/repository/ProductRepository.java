package com.andromeda.dreamshops.repository;

import com.andromeda.dreamshops.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryName(String category);

    List<Product> findByBrand(String brand);

    List<Product> findByCategoryNameAndBrand(String category, String brand);

    List<Product> findByName(String name);

    List<Product> findByBrandAndName(String brand, String name);

    Long countByBrandAndName(String brand, String name);

    boolean existsByNameAndBrand(String name, String brand);

    //shop related queries
    Optional<Product> findByIdAndShopId(Long productId, Long shopId);
    List<Product> findByShopId(Long shopId);

    //Rio Electronics
    List<Product> findByShopName(String shopName);

    //Rio Electronics , Laptops
    List<Product> findByShopNameAndCategoryName(String shopName, String categoryName);

    //Rio Electronics , Apple
    List<Product> findByShopNameAndBrand(String shopName, String brand);

    //Rio Electronics , Apple , Laptops
    List<Product> findByShopNameAndBrandAndCategoryName(String shopName, String brand, String categoryName);

    //Rio Electronics , MacBook Pro 14
    Optional<Product> findByShopNameAndName(String shopName, String name);

    Long countByShopId(Long shopId);// Counting all products in a shop

    boolean existsByNameAndShopName(String name, String shopName);

}
