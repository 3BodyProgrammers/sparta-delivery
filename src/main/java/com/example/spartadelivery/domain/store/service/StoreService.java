package com.example.spartadelivery.domain.store.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.config.LocalTimeConverter;
import com.example.spartadelivery.domain.menu.dto.response.MenuResponseDto;
import com.example.spartadelivery.domain.store.dto.request.StoreSaveRequestDto;
import com.example.spartadelivery.domain.store.dto.response.StoreDetailResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreSaveResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
//TODO : User 구현 시 AuthUser 사용
//TODO : 휴일 구현 시 휴일 적용

    private final StoreRepository storeRepository;

    public StoreSaveResponseDto save(Long userId, String userRole, StoreSaveRequestDto request) {
        if (!isOwner(userRole)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "가게 생성은 사장님만 가능합니다.");
        }

        if (storeRepository.countByUserId(userId) >= 3) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "가게 생성은 인당 최대 3개 까지만 가능합니다.");
        }

        if (storeRepository.existsByName(request.getName())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 가게 이름이 이미 존재 합니다.");
        }

        Store store = Store.toEntity(request.getName(), LocalTimeConverter.toLocalTime(request.getOpenAt()), LocalTimeConverter.toLocalTime(request.getCloseAt()), request.getMinimumPrice(), userId, userRole);
        Store savedStore = storeRepository.save(store);
        return StoreSaveResponseDto.of(savedStore);
    }

    public Page<StoreResponseDto> getStores(String name, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Store> stores = storeRepository.findAllByNameContaining(name, pageable);
        return stores.map(StoreResponseDto::of);
    }

    public StoreDetailResponseDto getStore(Long id) {
        Store findStore = storeRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));
        //TODO : 해당 가게에 맞는 메뉴 리스트 조회
        List<MenuResponseDto> menuList = new ArrayList<>();
        return StoreDetailResponseDto.of(findStore, menuList);
    }

    public boolean isOwner(String userRole) {
        return "OWNER".equals(userRole);
    }
}
