package com.example.spartadelivery.domain.menu.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.spartadelivery.domain.menu.dto.request.MenuUpdateRequestDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuDeleteResponseDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuSaveResponseDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuUpdateResponseDto;
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

    public MenuSaveResponseDto saveMenu(Long storeId, Long userId, String userRole, MenuSaveRequestDto request) {
        if (!isOwner(userRole)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 생성은 사장님만 가능합니다.");
        }

        Store findStore = storeService.findById(storeId);

        if (!findStore.getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 생성은 해당 가게의 사장님만 가능합니다.");
        }

        if (menuRepository.existsByNameAndStore(storeId, request.getName())) {
            Menu findMenu = menuRepository.findByNameAndStore(storeId, request.getName());
            findMenu.restore();
            return MenuSaveResponseDto.of(findMenu);
        }

        Menu menu = Menu.toEntity(request, userId, userRole, storeId);
        menu = menuRepository.save(menu);

        return MenuSaveResponseDto.of(menu);
    }

    private boolean isOwner(String userRole) {
        return "OWNER".equals(userRole);
    }

    public MenuUpdateResponseDto updateMenu(Long id, Long storeId, Long userId, String userRole, MenuUpdateRequestDto request) {
        Menu findMenu = menuRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 메뉴는 존재하지 않습니다."));

        if (!isOwner(userRole)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 수정은 사장님만 가능합니다.");
        }

        Store findStore = storeService.findById(storeId);

        if (!findStore.getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 수정은 해당 가게의 사장님만 가능합니다.");
        }

        findMenu.update(request);

        return MenuUpdateResponseDto.of(findMenu);
    }

    public MenuDeleteResponseDto deleteMenu(Long storeId, Long id, Long userId, String userRole) {
        Menu findMenu = menuRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 메뉴는 존재하지 않습니다."));

        if (!isOwner(userRole)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 수정은 사장님만 가능합니다.");
        }

        Store findStore = storeService.findById(storeId);

        if (!findStore.getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 수정은 해당 가게의 사장님만 가능합니다.");
        }

        findMenu.delete(request);

        return MenuDeleteResponseDto.of("메뉴가 성공적으로 삭제되었습니다.");
    }
}
