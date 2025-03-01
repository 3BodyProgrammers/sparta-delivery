package com.example.spartadelivery.domain.store.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.store.dto.request.StoreSaveRequestDto;
import com.example.spartadelivery.domain.store.dto.request.StoreUpdateRequestDto;
import com.example.spartadelivery.domain.store.dto.response.StoreDetailResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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

    @Nested
    class storeSaveTest {
        private StoreSaveRequestDto storeSaveRequestDto;

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
            assertThatThrownBy(() -> storeService.saveStore(userId, userRole, storeSaveRequestDto))
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
            assertThatThrownBy(() -> storeService.saveStore(userId, userRole, storeSaveRequestDto))
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
            assertThatThrownBy(() -> storeService.saveStore(userId, userRole, storeSaveRequestDto))
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
            StoreSaveResponseDto response = storeService.saveStore(userId, userRole, storeSaveRequestDto);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Test Store");
        }
    }

    @Nested
    class storeGetTest {

        private Store store1, store2;
        private Pageable pageable;

        @BeforeEach
        void setUp() {
            store1 = Store.toEntity("Store A", null, null, 10000, 1L, "OWNER");
            store2 = Store.toEntity("Store B", null, null, 20000, 2L, "OWNER");

            pageable = PageRequest.of(0, 10);
        }

        @Test
        public void 이름_없이_전체_조회_시_성공() {
            // given
            Page<Store> stores = new PageImpl<>(List.of(store1, store2), pageable, 2);
            given(storeRepository.findAllByNameContaining(null, pageable)).willReturn(stores);

            // when
            Page<StoreResponseDto> result = storeService.getStores(null, 1, 10);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Store A");
            assertThat(result.getContent().get(1).getName()).isEqualTo("Store B");
        }

        @Test
        public void 이름_포함_전체_조회_시_성공_1() {
            // given
            Page<Store> stores = new PageImpl<>(List.of(store1, store2), pageable, 2);
            given(storeRepository.findAllByNameContaining("Store", pageable)).willReturn(stores);

            // when
            Page<StoreResponseDto> result = storeService.getStores("Store", 1, 10);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Store A");
            assertThat(result.getContent().get(1).getName()).isEqualTo("Store B");
        }

        @Test
        public void 이름_포함_전체_조회_시_성공_2() {
            // given
            Page<Store> stores = new PageImpl<>(List.of(store1), pageable, 1);
            given(storeRepository.findAllByNameContaining("A", pageable)).willReturn(stores);

            // when
            Page<StoreResponseDto> result = storeService.getStores("A", 1, 10);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Store A");
        }

        @Test
        public void 단건_조회_시_가게가_존재_하지_않는_다면_실패() {
            // given
            long storeId = 1L;
            given(storeRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> storeService.getStore(storeId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게는 존재하지 않습니다.");
        }

        @Test
        public void 단건_조회_시_가게가_존재_하는_경우_성공() {
            // given
            long storeId = 1L;
            Store store = Store.toEntity("Store A", null, null, 10000, 1L, "OWNER");
            given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

            // when
            StoreDetailResponseDto findStore = storeService.getStore(storeId);

            // then
            assertThat(findStore).isNotNull();
            assertThat(findStore.getName()).isEqualTo("Store A");
        }
    }


    @Nested
    class storeUpdateTest {

        private Store store;
        private StoreUpdateRequestDto updateRequest;

        @BeforeEach
        void setUp() {
            store = Store.toEntity("Old Store", null, null, 10000, 1L, "OWNER");
            updateRequest = new StoreUpdateRequestDto("Updated Store", "08:00", "12:00", 20000);
        }

        @Test
        public void 수정_하려는_가게가_없다면_실패() {
            // given
            Long storeId = 1L;
            Long userId = 1L;
            String userRole = "OWNER";

            // when & then
            assertThatThrownBy(() -> storeService.updateStore(storeId, userId, userRole, updateRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게는 존재하지 않습니다.");
        }

        @Test
        public void 수정_하려는_사용자가_사장님이_아니면_실패() {
            // given
            Long storeId = 1L;
            Long userId = 2L;
            String userRole = "USER";
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

            // when & then
            assertThatThrownBy(() -> storeService.updateStore(storeId, userId, userRole, updateRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("가게 수정은 사장님만 가능합니다.");
        }

        @Test
        public void 수정_하려는_사용자와_해당_가게의_사장님의_정보가_일치하지_않으면_실패() {
            // given
            Long storeId = 1L;
            Long userId = 2L;
            String userRole = "OWNER";
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

            // when & then
            assertThatThrownBy(() -> storeService.updateStore(storeId, userId, userRole, updateRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessage( "가게 수정은 가게의 사장님만 가능 합니다.");
        }

        @Test
        public void 수정_성공() {
            // given
            Long storeId = 1L;
            Long userId = 1L;
            String userRole = "OWNER";

            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

            // when
            StoreResponseDto response = storeService.updateStore(storeId, userId, userRole, updateRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Updated Store");
            assertThat(response.getMinimumPrice()).isEqualTo(20000);
            assertThat(response.getOpenAt()).isEqualTo(LocalTime.of(8, 0));
        }
    }
}