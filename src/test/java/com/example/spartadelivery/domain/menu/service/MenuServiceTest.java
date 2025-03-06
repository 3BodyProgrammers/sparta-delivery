package com.example.spartadelivery.domain.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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
import com.example.spartadelivery.domain.store.service.StoreGetService;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreGetService storeGetService;

    @InjectMocks
    private MenuService menuService;

    @Nested
    class menuSaveTest {

        private AuthUser authUser;
        private User user;
        private Store store;
        private MenuSaveRequestDto request;

        @BeforeEach
        void setUp() {
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.OWNER);
            user = User.fromAuthUser(authUser);
            store = Store.toEntity("Store", LocalTime.of(8, 0), LocalTime.of(22, 0), 10000, user);
            request = new MenuSaveRequestDto("Menu", 10000);
        }

        @Test
        void 메뉴_생성_시_가게가_존재하지_않으면_실패() {
            //Given
            Long storeId = 1L;
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willThrow(
                    new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));
            //When & Then
            assertThatThrownBy(() -> menuService.saveMenu(storeId, authUser, request))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게는 존재하지 않습니다.");
        }

        @Test
        void 메뉴_생성_시_요청한_사장님이_가게의_사장님이_아닌_경우_실패() {
            //Given
            Long storeId = 1L;
            AuthUser anotherUser = new AuthUser(2L, "bb@bb.com", "name", UserRole.OWNER);
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willReturn(store);

            //When & Then
            assertThatThrownBy(() -> menuService.saveMenu(storeId, anotherUser, request))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("메뉴 생성은 해당 가게의 사장님만 가능합니다.");
        }

        @Test
        void 메뉴_생성_시_삭제된_메뉴가_존재하는_경우_메뉴_수정_및_성공() {
            //Given
            Long storeId = 1L;
            Menu oldMenu = Menu.toEntity(
                    new MenuSaveRequestDto("oldName", 5000), user, store);
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willReturn(store);
            given(menuRepository.existsByStoreIdAndName(anyLong(), anyString())).willReturn(true);
            given(menuRepository.findByStoreIdAndName(anyLong(), anyString())).willReturn(oldMenu);

            //When
            MenuSaveResponseDto response = menuService.saveMenu(storeId, authUser, request);

            //Then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo(request.getName());
        }

        @Test
        void 메뉴_생성_시_성공() {
            //Given
            Long storeId = 1L;
            Menu menu = Menu.toEntity(request, user, store);
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willReturn(store);
            given(menuRepository.existsByStoreIdAndName(anyLong(), anyString())).willReturn(false);
            given(menuRepository.save(any())).willReturn(menu);

            //When
            MenuSaveResponseDto response = menuService.saveMenu(storeId, authUser, request);

            //Then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo(request.getName());
        }
    }

    @Nested
    class menuUpdateTest {

        private AuthUser authUser;
        private User user;
        private Store store;
        private Menu menu;
        private MenuUpdateRequestDto request;

        @BeforeEach
        void setUp() {
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.OWNER);
            user = User.fromAuthUser(authUser);
            store = Store.toEntity("Store", LocalTime.of(8, 0), LocalTime.of(22, 0), 10000, user);
            menu = Menu.toEntity(new MenuSaveRequestDto("oldName", 5000), user, store);
            request = new MenuUpdateRequestDto("newName", 10000);
        }

        @Test
        void 메뉴_수정_시_메뉴가_존재하지_않으면_실패() {
            //Given
            Long storeId = 1L;
            Long menuId = 1L;
            given(menuRepository.findById(anyLong())).willReturn(Optional.empty());
            //When & Then
            assertThatThrownBy(() -> menuService.updateMenu(menuId, storeId, authUser, request))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 메뉴는 존재하지 않습니다.");
        }

        @Test
        void 메뉴_수정_시_가게가_존재하지_않으면_실패() {
            //Given
            Long storeId = 1L;
            Long menuId = 1L;
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willThrow(
                    new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));
            //When & Then
            assertThatThrownBy(() -> menuService.updateMenu(menuId, storeId, authUser, request))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게는 존재하지 않습니다.");
        }

        @Test
        void 메뉴_수정_시_요청한_사장님이_가게의_사장님이_아닌_경우_실패() {
            //Given
            Long storeId = 1L;
            Long menuId = 1L;
            AuthUser anotherUser = new AuthUser(2L, "bb@bb.com", "name", UserRole.OWNER);
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willReturn(store);
            //When & Then
            assertThatThrownBy(() -> menuService.updateMenu(menuId, storeId, anotherUser, request))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("메뉴 수정은 해당 가게의 사장님만 가능합니다.");
        }

        @Test
        void 메뉴_수정_성공() {
            //Given
            Long menuId = 1L;
            Long storeId = 1L;
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willReturn(store);

            //When
            MenuUpdateResponseDto response = menuService.updateMenu(menuId, storeId, authUser, request);

            //Then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo(request.getName());
        }
    }

    @Nested
    class menuDeleteTest {

        private AuthUser authUser;
        private User user;
        private Store store;
        private Menu menu;

        @BeforeEach
        void setUp() {
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.OWNER);
            user = User.fromAuthUser(authUser);
            store = Store.toEntity("Store", LocalTime.of(8, 0), LocalTime.of(22, 0), 10000, user);
            menu = Menu.toEntity(new MenuSaveRequestDto("oldName", 5000), user, store);
        }

        @Test
        void 메뉴_삭제_시_메뉴가_존재하지_않으면_실패() {
            //Given
            Long storeId = 1L;
            Long menuId = 1L;
            given(menuRepository.findById(anyLong())).willReturn(Optional.empty());
            //When & Then
            assertThatThrownBy(() -> menuService.deleteMenu(menuId, storeId, authUser))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 메뉴는 존재하지 않습니다.");
        }

        @Test
        void 메뉴_삭제_시_가게가_존재하지_않으면_실패() {
            //Given
            Long storeId = 1L;
            Long menuId = 1L;
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willThrow(
                    new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));
            //When & Then
            assertThatThrownBy(() -> menuService.deleteMenu(menuId, storeId, authUser))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게는 존재하지 않습니다.");
        }

        @Test
        void 메뉴_삭제_시_요청한_사장님이_가게의_사장님이_아닌_경우_실패() {
            //Given
            Long storeId = 1L;
            Long menuId = 1L;
            AuthUser anotherUser = new AuthUser(2L, "bb@bb.com", "name", UserRole.OWNER);
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willReturn(store);
            //When & Then
            assertThatThrownBy(() -> menuService.deleteMenu(menuId, storeId, anotherUser))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("메뉴 삭제는 해당 가게의 사장님만 가능합니다.");
        }

        @Test
        void 메뉴_삭제_성공() {
            //Given
            Long menuId = 1L;
            Long storeId = 1L;
            given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
            given(storeGetService.findByIdAndDeletedAtIsNull(anyLong())).willReturn(store);

            //When
            MenuDeleteResponseDto response = menuService.deleteMenu(menuId, storeId, authUser);

            //Then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("메뉴가 성공적으로 삭제되었습니다.");
        }
    }

}