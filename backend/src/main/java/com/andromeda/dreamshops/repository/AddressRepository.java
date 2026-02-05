package com.andromeda.dreamshops.repository;

import com.andromeda.dreamshops.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findDefaultAddressByUserAccountId(Long userAccountId);
    List<Address> findAllByUserAccountId(Long userAccountId);
}
