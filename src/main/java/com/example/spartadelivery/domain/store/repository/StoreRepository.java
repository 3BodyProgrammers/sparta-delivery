package com.example.spartadelivery.domain.store.repository;

import com.example.spartadelivery.domain.store.entity.Store;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    int countByUserId(Long userId);

    boolean existsByName(String name);

    Page<Store> findAllByNameContaining(@Nullable String name, Pageable pageable);

}
