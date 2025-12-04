package vn.hieu4tuoi.common;

/**
 * Trạng thái bảo hành
 */
public enum WarrantyStatus {
    PENDING,           // Đang chờ
    CONFIRMED,         // Đã xác nhận
    IN_WARRANTY,       // Đang bảo hành
    RETURNING,         // Đang hoàn hàng
    COMPLETED,         // Đã bảo hành
    CANCELLED          // Đã hủy
}

