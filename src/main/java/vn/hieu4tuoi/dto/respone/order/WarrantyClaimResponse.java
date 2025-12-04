package vn.hieu4tuoi.dto.respone.order;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hieu4tuoi.common.WarrantyStatus;

/**
 * Response cho yêu cầu bảo hành
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarrantyClaimResponse {
    private String id;                      // Mã đơn bảo hành
    private String orderId;                 // Mã đơn hàng
    private WarrantyStatus status;          // Trạng thái
    private String reason;                  // Lý do
    private String phoneNumber;             // Số điện thoại
    private String address;                 // Địa chỉ
    private LocalDateTime createdAt;        // Ngày gửi yêu cầu
    private ProductWarrantyResponse product; // Sản phẩm
}

