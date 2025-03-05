package com.example.spartadelivery.domain.menu.repository;

import com.example.spartadelivery.domain.menu.entity.Menu;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    boolean existsByStoreIdAndName(Long storeId, String name);

    Menu findByStoreIdAndName(Long storeId, String name);

    List<Menu> findAllByStoreIdAndDeletedAtIsNull(Long id);

    @EntityGraph(
            attributePaths = "store"
    )
    Page<Menu> findAllByNameContainingAndDeletedAtIsNull(String name, Pageable pageable);
}
