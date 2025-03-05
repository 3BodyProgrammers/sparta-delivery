package com.example.spartadelivery.domain.store.repository;

import com.example.spartadelivery.domain.store.entity.Store;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    int countByUserId(Long userId);

    boolean existsByName(String name);

    @EntityGraph(
            attributePaths = {"user"}
    )
    Optional<Store> findByIdAndDeletedAtIsNull(Long id);

    Page<Store> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Store> findAllByNameContainingAndDeletedAtIsNull(@Param("name") String name, Pageable pageable);

    @EntityGraph(
            attributePaths = {"user"}
    )
    Optional<Store> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
}
