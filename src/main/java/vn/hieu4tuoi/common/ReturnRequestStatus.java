package vn.hieu4tuoi.common;

/**
 * Trạng thái trả hàng
 */
public enum ReturnRequestStatus {
    PENDING,           // Đang chờ
    CONFIRMED,         // Đã xác nhận
    REFUNDING,         // Đang hoàn tiền
    REFUNDED,          // Đã hoàn tiền
    CANCELLED          // Đã hủy
}

