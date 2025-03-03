package com.example.spartadelivery.domain.menu.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuSaveResponseDto;
import com.example.spartadelivery.domain.menu.entity.Menu;
import com.example.spartadelivery.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreService storeService;

    public MenuSaveResponseDto saveMenus(Long storeId, Long userId, String userRole, MenuSaveRequestDto request) {
        if (!isOwner(userRole)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 생성은 사장님만 가능합니다.");
        }

        Store findStore = storeService.findById(storeId);

        if (!findStore.getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 생성은 해당 가게의 사장님만 가능합니다.");
        }

        Menu menu = Menu.toEntity(request, userId, userRole, storeId);
        menu = menuRepository.save(menu);

        return MenuSaveResponseDto.of(menu);
    }

    private boolean isOwner(String userRole) {
        return "OWNER".equals(userRole);
    }
}
