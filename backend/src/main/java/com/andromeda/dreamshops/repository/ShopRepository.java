package com.andromeda.dreamshops.repository;

import com.andromeda.dreamshops.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Shop findByName(String name);

    boolean existsByName(String name);

    boolean existsByShopOwnerId(Long userId);

    Shop findByShopOwnerId(Long userId);
}
