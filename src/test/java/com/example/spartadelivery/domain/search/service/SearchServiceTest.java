package com.example.spartadelivery.domain.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.config.HolidayConverter;
import com.example.spartadelivery.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.spartadelivery.domain.menu.entity.Menu;
import com.example.spartadelivery.domain.menu.service.MenuGetService;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.service.StoreGetService;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private StoreGetService storeGetService;

    @Mock
    private MenuGetService menuGetService;

    @Mock
    private HolidayConverter holidayConverter;

    @InjectMocks
    private SearchService searchService;

    private Page<Store> stores;
    private Page<Store> menuStores;
    private Page<Menu> menus;

    @BeforeEach
    void setup() {
        Pageable pageable = PageRequest.of(0, 10);
        AuthUser authUser1 = new AuthUser(1L, "aa@aa.com", "name1", UserRole.OWNER);
        AuthUser authUser2 = new AuthUser(2L, "bb@bb.com", "name2", UserRole.OWNER);
        AuthUser authUser3 = new AuthUser(3L, "cc@cc.com", "name3", UserRole.OWNER);
        AuthUser authUser4 = new AuthUser(4L, "dd@dd.com", "name4", UserRole.OWNER);
        User user1 = User.fromAuthUser(authUser1);
        User user2 = User.fromAuthUser(authUser2);
        User user3 = User.fromAuthUser(authUser3);
        User user4 = User.fromAuthUser(authUser4);
        List<Store> storeList = new ArrayList<>();


        for (int i = 1; i <= 3; i++) {
            Store store = Store.toEntity("덮밥" + i, LocalTime.of(8, 0), LocalTime.of(20, 0), 10000, "Store Notice", user2);
            storeList.add(store);
        }

        menuStores = new PageImpl<>(storeList, pageable, 10);
        storeList = new ArrayList<>();
        
        for (int i = 4; i <= 6; i++) {
            Store store = Store.toEntity("김밥" + i, LocalTime.of(9, 0), LocalTime.of(18, 0), 10000, "Store Notice", user2);
            storeList.add(store);
        }
        for (int i = 7; i <= 9; i++) {
            Store store = Store.toEntity("김밥" + i, LocalTime.of(10, 0), LocalTime.of(23, 0), 10000, "Store Notice", user3);
            storeList.add(store);
        }
        for (int i = 10; i <= 12; i++) {
            Store store = Store.toEntity("김밥" + i, LocalTime.of(7, 0), LocalTime.of(13, 0), 10000, "Store Notice", user4);
            storeList.add(store);
        }

        List<Menu> menuList = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            Menu menu = Menu.toEntity(new MenuSaveRequestDto("김밥" + i, 12000), user1, storeList.get(i));
            menuList.add(menu);
        }

        stores = new PageImpl<>(storeList, pageable, 10);
        menus = new PageImpl<>(menuList, pageable, 10);
    }

    @Test
    void 통합_검색_성공() {
        //Given
        given(storeGetService.findAllByNameContainingAndDeletedAtIsNull(any(), any())).willReturn(stores);
        given(menuGetService.findAllByNameContainingAndDeletedAtIsNull(any(), any())).willReturn(menus);
        given(storeGetService.findStoresByIds(any(), any())).willReturn(menuStores);
        given(holidayConverter.convertToEntityAttribute(any())).willReturn(new ArrayList<>());
        //When
        Page<StoreResponseDto> response = searchService.search("김밥", 1, 10);

        //Then
        assertThat(response).isNotNull();
        assertThat(response.stream().toList().get(0).getName()).containsIgnoringCase("김밥");
    }

}