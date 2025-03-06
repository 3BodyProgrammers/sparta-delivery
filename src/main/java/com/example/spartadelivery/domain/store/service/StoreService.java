package com.example.spartadelivery.domain.store.service;

import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.config.HolidayConverter;
import com.example.spartadelivery.config.LocalTimeConverter;
import com.example.spartadelivery.domain.menu.dto.response.MenuForStoreResponseDto;
import com.example.spartadelivery.domain.menu.entity.Menu;
import com.example.spartadelivery.domain.menu.service.MenuGetService;
import com.example.spartadelivery.domain.store.dto.request.StoreSaveRequestDto;
import com.example.spartadelivery.domain.store.dto.request.StoreUpdateRequestDto;
import com.example.spartadelivery.domain.store.dto.response.*;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import com.example.spartadelivery.domain.user.entity.User;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MenuGetService menuGetService;
    private final LocalTimeConverter localTimeConverter;
    private final HolidayConverter holidayConverter;

    public StoreSaveResponseDto saveStore(AuthUser authUser, StoreSaveRequestDto request) {
        User user = User.fromAuthUser(authUser);

        if (storeRepository.countByUserId(user.getId()) >= 3) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "가게 생성은 인당 최대 3개 까지만 가능합니다.");
        }

        if (storeRepository.existsByName(request.getName())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 가게 이름이 이미 존재 합니다.");
        }

        Store store = Store.toEntity(request.getName(),
                localTimeConverter.convertToEntityAttribute(request.getOpenAt()),
                localTimeConverter.convertToEntityAttribute(request.getCloseAt()), request.getMinimumPrice(), request.getNotice(), user);
        Store savedStore = storeRepository.save(store);
        return StoreSaveResponseDto.of(savedStore);
    }

    @Transactional(readOnly = true)
    public Page<StoreResponseDto> getStores(String name, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Store> stores = name == null ? storeRepository.findAllByDeletedAtIsNull(pageable)
                : storeRepository.findAllByNameContainingAndDeletedAtIsNull(name, pageable);
        Map<Long, List<String>> holidaysMap = new HashMap<>();
        for (Store store : stores) {
            holidaysMap.put(store.getId(), holidayConverter.convertToEntityAttribute(store.getHoliday()));
        }

        return stores.map(store -> StoreResponseDto.of(store, holidaysMap.get(store.getId())));
    }

    @Transactional(readOnly = true)
    public StoreDetailResponseDto getStore(Long id) {
        Store findStore = storeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));
        List<String> holidays = holidayConverter.convertToEntityAttribute(findStore.getHoliday());
        List<MenuForStoreResponseDto> menuList = menuGetService.findAllByStoreIdAndDeletedAtIsNull(id).stream()
                .map(MenuForStoreResponseDto::of).toList();
        return StoreDetailResponseDto.of(findStore, holidays, menuList);
    }

    @Transactional
    public StoreUpdateResponseDto updateStore(Long id, AuthUser authUser, StoreUpdateRequestDto request) {
        User user = User.fromAuthUser(authUser);

        Store findStore = storeRepository.findByIdAndUserIdAndDeletedAtIsNull(id, user.getId())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));

        if (!findStore.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "가게 수정은 가게의 사장님만 가능 합니다.");
        }

        LocalTime openAt = localTimeConverter.convertToEntityAttribute(request.getOpenAt());
        LocalTime closeAt = localTimeConverter.convertToEntityAttribute(request.getCloseAt());
        findStore.update(request.getName(), openAt, closeAt, request.getMinimumPrice(), request.getNotice());
        List<String> holidays = holidayConverter.convertToEntityAttribute(findStore.getHoliday());

        return StoreUpdateResponseDto.of(findStore, holidays);
    }

    @Transactional
    public StoreDeleteResponseDto deleteStore(Long id, AuthUser authUser) {
        User user = User.fromAuthUser(authUser);

        Store findStore = storeRepository.findByIdAndUserIdAndDeletedAtIsNull(id, user.getId())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));

        if (!findStore.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "가게 폐업은 가게의 사장님만 가능 합니다.");
        }

        findStore.delete();
        menuGetService.findAllByStoreIdAndDeletedAtIsNull(id).forEach(Menu::delete);

        return StoreDeleteResponseDto.of("폐업 되었습니다.");
    }

}
