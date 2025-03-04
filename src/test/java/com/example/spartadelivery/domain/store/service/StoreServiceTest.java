package com.example.spartadelivery.domain.store.service;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.config.HolidayConverter;
import com.example.spartadelivery.config.LocalTimeConverter;
import com.example.spartadelivery.domain.store.dto.request.StoreSaveRequestDto;
import com.example.spartadelivery.domain.store.dto.request.StoreUpdateRequestDto;
import com.example.spartadelivery.domain.store.dto.response.StoreDeleteResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreDetailResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreSaveResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import java.util.ArrayList;
import org.hibernate.cache.spi.support.AbstractRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private HolidayConverter holidayConverter;

    @Mock
    private LocalTimeConverter localTimeConverter;

    @InjectMocks
    private StoreService storeService;

    @Nested
    class storeSaveTest {
        private StoreSaveRequestDto storeSaveRequestDto;
        private AuthUser authUser;

        @BeforeEach
        void setUp() {
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.OWNER);
            storeSaveRequestDto = new StoreSaveRequestDto("Test Store", "08:00", "22:00", 10000);
        }

        @Test
        public void 가게_등록_시_해당_사장님이_이미_가게가_3개_이상_존재하는_경우_실패() {
            // given
            given(storeRepository.countByUserId(anyLong())).willReturn(3);

            // when & then
            assertThatThrownBy(() -> storeService.saveStore(authUser, storeSaveRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("가게 생성은 인당 최대 3개 까지만 가능합니다.");
        }

        @Test
        public void 가게_등록_시_이미_가게_이름이_존재하는_경우_실패() {
            // given
            given(storeRepository.existsByName(anyString())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> storeService.saveStore(authUser, storeSaveRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게 이름이 이미 존재 합니다.");
        }


        @Test
        public void 가게_등록_시_성공() {
            // given
            User user = User.fromAuthUser(authUser);
            Store store = Store.toEntity("Test Store", LocalTime.of(8, 0), LocalTime.of(22, 0), 10000, user);
            given(storeRepository.countByUserId(anyLong())).willReturn(0); // 가게 개수 제한 미달
            given(storeRepository.existsByName(anyString())).willReturn(false); // 중복 가게 없음
            given(storeRepository.save(any())).willReturn(store);

            // when
            StoreSaveResponseDto response = storeService.saveStore(authUser, storeSaveRequestDto);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Test Store");
        }
    }

    @Nested
    class storeGetsTest {

        private Store store1, store2;
        private Pageable pageable;

        @BeforeEach
        void setUp() {
            AuthUser authUser1 = new AuthUser(1L, "aa@aa.com", "name1", UserRole.OWNER);
            User user1 = User.fromAuthUser(authUser1);
            AuthUser authUser2 = new AuthUser(2L, "bb@bb.com", "name2", UserRole.OWNER);
            User user2 = User.fromAuthUser(authUser2);
            store1 = Store.toEntity("Store A", null, null, 10000, user1);
            store2 = Store.toEntity("Store B", null, null, 20000, user2);

            pageable = PageRequest.of(0, 10);
        }

        @Test
        public void 이름_없이_전체_조회_시_성공() {
            // given
            Page<Store> stores = new PageImpl<>(List.of(store1, store2), pageable, 2);
            given(storeRepository.findAllByDeletedAtIsNull(pageable)).willReturn(stores);

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
            given(storeRepository.findAllByNameContainingAndDeletedAtIsNull("Store", pageable)).willReturn(stores);

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
            given(storeRepository.findAllByNameContainingAndDeletedAtIsNull("A", pageable)).willReturn(stores);

            // when
            Page<StoreResponseDto> result = storeService.getStores("A", 1, 10);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Store A");
        }
    }

    @Nested
    class storeGetTest{

        @Test
        public void 단건_조회_시_가게가_존재_하지_않는_다면_실패() {
            // given
            long storeId = 1L;
            given(storeRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> storeService.getStore(storeId))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게는 존재하지 않습니다.");
        }

        @Test
        public void 단건_조회_시_가게가_존재_하는_경우_성공() {
            // given
            AuthUser authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.OWNER);
            User user = User.fromAuthUser(authUser);
            long storeId = 1L;
            Store store = Store.toEntity("Store A", null, null, 10000, user);
            given(storeRepository.findByIdAndDeletedAtIsNull(anyLong())).willReturn(Optional.of(store));

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
        private AuthUser authUser;
        private User user;

        @BeforeEach
        void setUp() {
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.OWNER);
            user = User.fromAuthUser(authUser);
            store = Store.toEntity("Old Store", null, null, 10000, user);
            updateRequest = new StoreUpdateRequestDto("Updated Store", "10:00", "22:00", 20000);
        }

        @Test
        public void 수정_하려는_가게가_없다면_실패() {
            // given
            Long storeId = 1L;

            // when & then
            assertThatThrownBy(() -> storeService.updateStore(storeId, authUser, updateRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게는 존재하지 않습니다.");
        }

        @Test
        public void 수정_하려는_사용자와_해당_가게의_사장님의_정보가_일치하지_않으면_실패() {
            // given
            Long storeId = 1L;
            AuthUser authUser2 = new AuthUser(2L, "bb@bb.com", "name2", UserRole.OWNER);
            given(storeRepository.findByIdAndUserIdAndDeletedAtIsNull(anyLong(), anyLong())).willReturn(Optional.of(store));

            // when & then
            assertThatThrownBy(() -> storeService.updateStore(storeId, authUser2, updateRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("가게 수정은 가게의 사장님만 가능 합니다.");
        }

        @Test
        public void 수정_성공() {
            // given
            Long storeId = 1L;
            Mockito.when(storeRepository.findByIdAndUserIdAndDeletedAtIsNull(storeId, user.getId()))
                    .thenReturn(Optional.of(store));

            Mockito.when(localTimeConverter.convertToEntityAttribute("10:00")).thenReturn(LocalTime.of(10, 0));
            Mockito.when(localTimeConverter.convertToEntityAttribute("22:00")).thenReturn(LocalTime.of(22, 0));

            Mockito.when(holidayConverter.convertToEntityAttribute(store.getHoliday()))
                    .thenReturn(List.of("Sunday"));

            // When
            StoreResponseDto result = storeService.updateStore(storeId, authUser, updateRequest);

            // Then
            assertNotNull(result);
            assertThat("Updated Store").isEqualTo(result.getName());
            assertThat(LocalTime.of(10, 0)).isEqualTo(store.getOpenAt());
            assertThat(LocalTime.of(22, 0)).isEqualTo(store.getCloseAt());
            assertThat(List.of("Sunday")).isEqualTo(result.getHolidays());

            Mockito.verify(storeRepository).findByIdAndUserIdAndDeletedAtIsNull(storeId, user.getId());
            Mockito.verify(localTimeConverter).convertToEntityAttribute("10:00");
            Mockito.verify(localTimeConverter).convertToEntityAttribute("22:00");
            Mockito.verify(holidayConverter).convertToEntityAttribute(store.getHoliday());
        }
    }

    @Nested
    class storeDeleteTest {

        private Store store;
        private AuthUser authUser;
        private User user;

        @BeforeEach
        void setUp() {
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.OWNER);
            user = User.fromAuthUser(authUser);
            store = Store.toEntity("Store", null, null, 10000, user);
        }


        @Test
        public void 폐업_하려는_가게가_없다면_실패() {
            // given
            Long storeId = 1L;
            given(storeRepository.findByIdAndUserIdAndDeletedAtIsNull(anyLong(), anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> storeService.deleteStore(storeId, authUser))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게는 존재하지 않습니다.");
        }

        @Test
        public void 폐업_요청한_사용자가_해당_가게의_사장님과_일치하지_않을_경우_실패() {
            // given
            Long storeId = 1L;
            AuthUser authUser2 = new AuthUser(2L, "bb@bb.com", "name2", UserRole.OWNER);
            given(storeRepository.findByIdAndUserIdAndDeletedAtIsNull(anyLong(), anyLong())).willReturn(Optional.of(store));

            // when & then
            assertThatThrownBy(() -> storeService.deleteStore(storeId, authUser2))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("가게 폐업은 가게의 사장님만 가능 합니다.");
        }

        @Test
        public void 폐업_성공() {
            // given
            Long storeId = 1L;
            given(storeRepository.findByIdAndUserIdAndDeletedAtIsNull(anyLong(), anyLong())).willReturn(Optional.of(store));

            // when
            StoreDeleteResponseDto response = storeService.deleteStore(storeId, authUser);

            // then
            assertThat(response.getMessage()).isEqualTo("폐업 되었습니다.");
        }
    }
}