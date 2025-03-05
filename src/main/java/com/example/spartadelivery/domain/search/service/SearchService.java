package com.example.spartadelivery.domain.search.service;

import com.example.spartadelivery.config.HolidayConverter;
import com.example.spartadelivery.domain.menu.entity.Menu;
import com.example.spartadelivery.domain.menu.service.MenuGetService;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.service.StoreGetService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final StoreGetService storeGetService;
    private final HolidayConverter holidayConverter;
    private final MenuGetService menuGetService;


    public Page<StoreResponseDto> search(String name, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Store> stores = storeGetService.findAllByNameContainingAndDeletedAtIsNull(name, pageable);
        Page<Menu> menus = menuGetService.findAllByNameContainingAndDeletedAtIsNull(name, pageable);
        List<Long> storeIdsFromMenus = menus.getContent().stream()
                .map(menu -> menu.getStore().getId())
                .distinct()
                .toList();
        Page<Store> menuStores = storeGetService.findStoresByIds(storeIdsFromMenus, pageable);
        Set<Store> totalStores = new HashSet<>();
        totalStores.addAll(stores.stream().toList());
        totalStores.addAll(menuStores.stream().toList());

        Map<Long, List<String>> holidaysMap = new HashMap<>();
        for (Store store : totalStores) {
            holidaysMap.put(store.getId(), holidayConverter.convertToEntityAttribute(store.getHoliday()));
        }

        List<StoreResponseDto> results = totalStores.stream().map(store -> StoreResponseDto.of(store, holidaysMap.get(store.getId()))).toList();

        return new PageImpl<>(results, pageable, size);
    }
}
