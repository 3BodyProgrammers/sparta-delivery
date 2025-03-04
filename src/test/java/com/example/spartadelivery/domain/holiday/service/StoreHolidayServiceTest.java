package com.example.spartadelivery.domain.holiday.service;

import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.config.HolidayConverter;
import com.example.spartadelivery.domain.holiday.dto.request.StoreHolidayRequestDto;
import com.example.spartadelivery.domain.holiday.enums.Holiday;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreHolidayServiceTest {

    @InjectMocks
    private StoreHolidayService storeHolidayService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private HolidayConverter holidayConverter;

    private Store store;
    private StoreHolidayRequestDto request;
    private AuthUser authUser;
    private User user;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.OWNER);
        user = User.fromAuthUser(authUser);
        store = Store.toEntity("Test Store", null, null, 10000, user);
        request = new StoreHolidayRequestDto(List.of(Holiday.MONDAY.getDay(), Holiday.WEDNESDAY.getDay()));
    }

    @Test
    void 가게_휴일_수정_시_가게가_존재_하지_않는_경우_실패() {
        // given
        Long storeId = 1L;
        Long userId = 1L;
        String userRole = "OWNER";
        given(storeRepository.findByIdAndUserIdAndDeletedAtIsNull(anyLong(), anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeHolidayService.updateHolidays(storeId, authUser, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 가게는 존재하지 않습니다.");
    }

    @Test
    void 가게_휴일_수정_시_요청한_사용자가_가게_사장님이_아닌_경우_실패() {
        // given
        Long storeId = 1L;
        AuthUser authUser2 = new AuthUser(2L, "bb@bb.com", "name2", UserRole.OWNER);
        given(storeRepository.findByIdAndUserIdAndDeletedAtIsNull(anyLong(), anyLong())).willReturn(Optional.of(store));

        // when & then
        assertThatThrownBy(() -> storeHolidayService.updateHolidays(storeId, authUser2, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("가게 휴일 수정은 가게의 사장님만 가능 합니다.");
    }

    @Test
    void 가게_휴일_수정_성공() {
        // given
        Long storeId = 1L;
        Integer holiday = 5;
        given(storeRepository.findByIdAndUserIdAndDeletedAtIsNull(anyLong(), anyLong())).willReturn(Optional.of(store));
        given(holidayConverter.convertToDatabaseColumn(any())).willReturn(holiday);
        given(holidayConverter.convertToEntityAttribute(any())).willReturn(List.of(Holiday.MONDAY.getDay(), Holiday.WEDNESDAY.getDay()));

        // when
        StoreResponseDto response = storeHolidayService.updateHolidays(storeId, authUser, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getHolidays()).containsExactlyInAnyOrder(Holiday.MONDAY.getDay(), Holiday.WEDNESDAY.getDay());
        verify(storeRepository, never()).save(any(Store.class));
    }
}