package com.example.spartadelivery.domain.store.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.store.dto.request.StoreSaveRequestDto;
import com.example.spartadelivery.domain.store.dto.response.StoreSaveResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    private StoreSaveRequestDto storeSaveRequestDto;

    @Nested
    class storeSaveTest{
        @BeforeEach
        void setUp() {
            storeSaveRequestDto = new StoreSaveRequestDto("Test Store", "08:00", "22:00", 10000);
        }

        @Test
        public void 가게_등록_시_사장님이_아니면_실패() {
            // given
            Long userId = 1L;
            String userRole = "USER";

            // when & then
            assertThatThrownBy(() -> storeService.save(userId, userRole, storeSaveRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("가게 생성은 사장님만 가능합니다.");
        }

        @Test
        public void 가게_등록_시_해당_사장님이_이미_가게가_3개_이상_존재하는_경우_실패() {
            // given
            Long userId = 1L;
            String userRole = "OWNER";

            given(storeRepository.countByUserId(anyLong())).willReturn(3);

            // when & then
            assertThatThrownBy(() -> storeService.save(userId, userRole, storeSaveRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("가게 생성은 인당 최대 3개 까지만 가능합니다.");
        }

        @Test
        public void 가게_등록_시_이미_가게_이름이_존재하는_경우_실패() {
            // given
            Long userId = 1L;
            String userRole = "OWNER";

            given(storeRepository.existsByName(anyString())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> storeService.save(userId, userRole, storeSaveRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게 이름이 이미 존재 합니다.");
        }


        @Test
        public void 가게_등록_시_성공() {
            // given
            Long userId = 1L;
            String userRole = "OWNER";
            Store store = Store.toEntity("Test Store", LocalTime.of(8, 0), LocalTime.of(22, 0), 10000, 1L, "OWNER");

            given(storeRepository.countByUserId(anyLong())).willReturn(0); // 가게 개수 제한 미달
            given(storeRepository.existsByName(anyString())).willReturn(false); // 중복 가게 없음
            given(storeRepository.save(any())).willReturn(store);

            // when
            StoreSaveResponseDto response = storeService.save(userId, userRole, storeSaveRequestDto);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Test Store");
        }
    }

}