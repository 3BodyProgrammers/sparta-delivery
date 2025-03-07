package com.example.spartadelivery.domain.store.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    public Store findByIdAndDeletedAtIsNull(Long storeId) {
        return storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 가게는 존재하지 않습니다."));
    }

    public boolean isWithinBusinessHours(Store store, LocalDateTime now) {
        LocalTime currentTime = now.toLocalTime();
        LocalTime openTime = store.getOpenAt();
        LocalTime closeTime = store.getCloseAt();

        // closeAt이 openAt보다 빠른 경우 (야간 영업 고려)
        if (closeTime.isBefore(openTime)) {
            return currentTime.isAfter(openTime) || currentTime.isBefore(closeTime);
        }

        // 영업 종료 시간이 자정 전인 경우
        return currentTime.isAfter(openTime) && currentTime.isBefore(closeTime);
    }

    public List<Store> findAllByUserId(Long id) {
        return storeRepository.findAllByUserId(id);
    }
}
