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
import com.example.spartadelivery.domain.store.dto.response.StoreDeleteResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreDetailResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreSaveResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import com.example.spartadelivery.domain.user.entity.User;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                localTimeConverter.convertToEntityAttribute(request.getCloseAt()), request.getMinimumPrice(), user);
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
    public StoreResponseDto updateStore(Long id, AuthUser authUser, StoreUpdateRequestDto request) {
        User user = User.fromAuthUser(authUser);

        Store findStore = storeRepository.findByIdAndUserIdAndDeletedAtIsNull(id, user.getId())
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));

        if (!findStore.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "가게 수정은 가게의 사장님만 가능 합니다.");
        }

        LocalTime openAt = localTimeConverter.convertToEntityAttribute(request.getOpenAt());
        LocalTime closeAt = localTimeConverter.convertToEntityAttribute(request.getCloseAt());
        findStore.update(request.getName(), openAt, closeAt, request.getMinimumPrice());
        List<String> holidays = holidayConverter.convertToEntityAttribute(findStore.getHoliday());

        return StoreResponseDto.of(findStore, holidays);
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

    public Store findByIdAndDeletedAtIsNull(Long storeId) {
        return storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));
    }


    //OrderService import
    public Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다."));
    }

    public boolean isDeletedStore(Long storeId) {
        Store store = findStoreById(storeId);
        return store.getDeletedAt() != null;
    }

    public boolean isWithinBusinessHours(Store store, LocalDateTime now) {
        return now.toLocalTime().isAfter(store.getOpenAt()) && now.toLocalTime().isBefore(store.getCloseAt());
    } // 홀리데이 있으므로 시간만 비교

    public List<Store> findStoresByOwnerId(Long ownerId) {
        return storeRepository.findAllByUserId(ownerId);
    }

}
