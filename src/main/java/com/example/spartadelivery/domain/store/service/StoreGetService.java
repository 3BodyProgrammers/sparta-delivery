package com.example.spartadelivery.domain.store.service;

import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreGetService {

    private final StoreRepository storeRepository;

    public Page<Store> findAllByNameContainingAndDeletedAtIsNull(String name, Pageable pageable) {
        return storeRepository.findAllByNameContainingAndDeletedAtIsNull(name, pageable);
    }

    public Page<Store> findStoresByIds(List<Long> storeIdsFromMenus, Pageable pageable) {
        return storeRepository.findByIdIn(storeIdsFromMenus, pageable);
    }
}
