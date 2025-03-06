package com.example.spartadelivery.domain.storedashboard.service;

import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.order.service.OrderGetService;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.service.StoreGetService;
import com.example.spartadelivery.domain.storedashboard.dto.StoreDashBoardDayResponseDto;
import com.example.spartadelivery.domain.storedashboard.dto.StoreDashBoardPeriodResponseDto;
import com.example.spartadelivery.domain.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreDashBoardService {

    private final OrderGetService orderGetService;
    private final StoreGetService storeGetService;

    public StoreDashBoardDayResponseDto getDailyStat(Long id, AuthUser authUser, String day) {
        if (isInValidateDateRange(day, LocalDate.now().toString())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "입력일은 과거여야 합니다.");
        }

        Store findStore = storeGetService.findByIdAndDeletedAtIsNull(id);
        User user = User.fromAuthUser(authUser);
        if (!findStore.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "가게 폐업은 가게의 사장님만 가능 합니다.");
        }

        LocalDateTime[] date = getLocalDateTime(day, day);

        List<Order> orders = orderGetService.findAllByStoreIdAndDeletedAtBetweenAndStatus(id, date[0], date[1]);
        int dailyUser = orders.size();
        int dailySales = orders.stream().mapToInt(Order::getPrice).sum();
        return StoreDashBoardDayResponseDto.of(LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                dailyUser, dailySales);
    }


    public StoreDashBoardPeriodResponseDto getPeriodStat(Long id, AuthUser authUser, String startDay, String endDay) {
        if (isInValidateDateRange(endDay, LocalDate.now().toString()) && isInValidateDateRange(startDay, endDay)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "종료일은 과거이거나, 시작일이 종료일보다 앞이어야 합니다.");
        }

        Store findStore = storeGetService.findByIdAndDeletedAtIsNull(id);
        User user = User.fromAuthUser(authUser);

        if (!findStore.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "가게 폐업은 가게의 사장님만 가능 합니다.");
        }

        LocalDateTime[] date = getLocalDateTime(startDay, endDay);

        List<Order> orders = orderGetService.findAllByStoreIdAndDeletedAtBetweenAndStatus(id, date[0], date[1]);
        int totalUser = orders.size();
        int totalSales = orders.stream().mapToInt(Order::getPrice).sum();
        List<StoreDashBoardDayResponseDto> dailyStats = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getDeletedAt().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                orderList -> StoreDashBoardDayResponseDto.of(
                                        LocalDate.from(orderList.get(0).getDeletedAt()),
                                        orderList.size(),
                                        orderList.stream().mapToInt(Order::getPrice).sum()
                                )
                        )
                ))
                .values()
                .stream()
                .toList();

        return StoreDashBoardPeriodResponseDto.of(
                LocalDate.parse(startDay, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                LocalDate.parse(endDay, DateTimeFormatter.ofPattern("yyyy-MM-dd")), totalUser, totalSales,
                dailyStats);
    }

    private boolean isInValidateDateRange(String startDay, String endDay) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDay, formatter);
        LocalDate endDate = LocalDate.parse(endDay, formatter);
        return startDate.isAfter(endDate);
    }

    private LocalDateTime[] getLocalDateTime(String startDay, String endDay) {
        LocalDateTime[] result = new LocalDateTime[2];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        result[0] = LocalDate.parse(startDay, formatter).atStartOfDay();
        result[1] = LocalDate.parse(endDay, formatter).atTime(23, 59, 59);
        return result;
    }
}
