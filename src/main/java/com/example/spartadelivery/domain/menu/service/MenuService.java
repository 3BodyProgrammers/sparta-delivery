package com.example.spartadelivery.domain.menu.service;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.spartadelivery.domain.menu.dto.request.MenuUpdateRequestDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuDeleteResponseDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuSaveResponseDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuUpdateResponseDto;
import com.example.spartadelivery.domain.menu.entity.Menu;
import com.example.spartadelivery.domain.menu.repository.MenuRepository;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.service.StoreService;
import com.example.spartadelivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreService storeService;

    public MenuSaveResponseDto saveMenu(Long storeId, AuthUser authUser, MenuSaveRequestDto request) {
        User user = User.fromAuthUser(authUser);

        Store findStore = storeService.findByIdAndDeletedAtIsNull(storeId);

        if (!findStore.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 생성은 해당 가게의 사장님만 가능합니다.");
        }

        if (menuRepository.existsByStoreIdAndName(storeId, request.getName())) {
            Menu findMenu = menuRepository.findByStoreIdAndName(storeId, request.getName());
            findMenu.restore();
            findMenu.update(request.getName(), request.getPrice());
            return MenuSaveResponseDto.of(findMenu, findStore.getName());
        }

        Menu menu = Menu.toEntity(request, user, findStore);
        menu = menuRepository.save(menu);

        return MenuSaveResponseDto.of(menu, findStore.getName());
    }

    public MenuUpdateResponseDto updateMenu(Long id, Long storeId, AuthUser authUser, MenuUpdateRequestDto request) {
        User user = User.fromAuthUser(authUser);

        Menu findMenu = menuRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 메뉴는 존재하지 않습니다."));

        Store findStore = storeService.findByIdAndDeletedAtIsNull(storeId);

        if (!findStore.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 수정은 해당 가게의 사장님만 가능합니다.");
        }

        findMenu.update(request.getName(), request.getPrice());

        return MenuUpdateResponseDto.of(findMenu, findStore.getName());
    }

    public MenuDeleteResponseDto deleteMenu(Long storeId, Long id, AuthUser authUser) {
        User user = User.fromAuthUser(authUser);

        Menu findMenu = menuRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 메뉴는 존재하지 않습니다."));

        Store findStore = storeService.findByIdAndDeletedAtIsNull(storeId);

        if (!findStore.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "메뉴 삭제는 해당 가게의 사장님만 가능합니다.");
        }

        findMenu.delete();

        return MenuDeleteResponseDto.of("메뉴가 성공적으로 삭제되었습니다.");
    }
}
