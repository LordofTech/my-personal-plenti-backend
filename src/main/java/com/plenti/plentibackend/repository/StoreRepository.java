package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Store entity
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    List<Store> findByType(String type);
}
