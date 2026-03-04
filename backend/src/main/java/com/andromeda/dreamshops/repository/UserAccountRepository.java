package com.andromeda.dreamshops.repository;

import com.andromeda.dreamshops.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findUserAccountByUserId(Long userId);
    boolean existsByUsername(String username);
    Optional<UserAccount> findByUsername(String username);
}
