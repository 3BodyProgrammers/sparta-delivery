package com.example.spartadelivery.domain.holiday.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.config.HolidayConverter;
import com.example.spartadelivery.domain.holiday.dto.request.StoreHolidayRequestDto;
import com.example.spartadelivery.domain.holiday.enums.Holiday;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
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

    @BeforeEach
    void setUp() {
        store = Store.toEntity("Test Store", null, null, 10000, 1L, "OWNER");
        request = new StoreHolidayRequestDto(List.of(Holiday.MONDAY, Holiday.WEDNESDAY));
    }


    @Test
    void 가게_휴일_수정_시_사장님이_아닌_경우_실패() {
        // given
        Long storeId = 1L;
        Long userId = 1L;
        String userRole = "USER";

        // when & then
        assertThatThrownBy(() -> storeHolidayService.updateHolidays(storeId, userId, userRole, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("가게 휴일 수정은 사장님만 가능합니다.");
    }

    @Test
    void 가게_휴일_수정_시_가게가_존재_하지_않는_경우_실패() {
        // given
        Long storeId = 1L;
        Long userId = 1L;
        String userRole = "OWNER";
        given(storeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> storeHolidayService.updateHolidays(storeId, userId, userRole, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("해당 가게는 존재하지 않습니다.");
    }

    @Test
    void 가게_휴일_수정_시_요청한_사용자가_가게_사장님이_아닌_경우_실패() {
        // given
        Long storeId = 1L;
        Long userId = 2L;
        String userRole = "OWNER";
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        // when & then
        assertThatThrownBy(() -> storeHolidayService.updateHolidays(storeId, userId, userRole, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("가게 휴일 수정은 가게의 사장님만 가능 합니다.");
    }

    @Test
    void 가게_휴일_수정_성공() {
        // given
        Long storeId = 1L;
        Long userId = 1L;
        String userRole = "OWNER";
        Integer holiday = 5;
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
        given(holidayConverter.convertToInteger(any())).willReturn(holiday);

        // when
        StoreResponseDto response = storeHolidayService.updateHolidays(storeId, userId, userRole, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getHolidays()).containsExactlyInAnyOrder(Holiday.MONDAY, Holiday.WEDNESDAY);
        verify(storeRepository, never()).save(any(Store.class));
    }
}