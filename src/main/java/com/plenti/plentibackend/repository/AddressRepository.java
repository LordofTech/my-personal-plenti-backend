package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Address entity
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserId(Long userId);
    
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
}
