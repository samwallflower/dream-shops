package com.andromeda.dreamshops.repository;

import com.andromeda.dreamshops.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    UserAccount findUserAccountByUserId(Long userId);
}
