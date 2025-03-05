package com.example.spartadelivery.domain.order.enums;

public enum OrderStatus {
    PENDING {
        @Override
        public boolean canChangeTo(OrderStatus nextStatus) {
            return nextStatus == ACCEPTED || nextStatus == CANCELED;
        }
    },
    ACCEPTED {
        @Override
        public boolean canChangeTo(OrderStatus nextStatus) {
            return nextStatus == DELIVERY;
        }
    },
    DELIVERY {
        @Override
        public boolean canChangeTo(OrderStatus nextStatus) { return nextStatus == COMPLETED; }
    },
    COMPLETED {
        @Override
        public boolean canChangeTo(OrderStatus nextStatus) {
            return false; // 완료된 주문은 변경 불가
        }
    },
    CANCELED {
        @Override
        public boolean canChangeTo(OrderStatus nextStatus) {
            return false; // 취소된 주문은 변경 불가
        }
    };

    public abstract boolean canChangeTo(OrderStatus nextStatus);
}
