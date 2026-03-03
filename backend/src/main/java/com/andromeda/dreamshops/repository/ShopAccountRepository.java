package com.andromeda.dreamshops.repository;

import com.andromeda.dreamshops.model.ShopAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopAccountRepository extends JpaRepository<ShopAccount, Long> {

    Optional<ShopAccount> findByShopId(Long shopId);
}
