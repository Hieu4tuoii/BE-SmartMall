package vn.hieu4tuoi.dto.respone.order;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hieu4tuoi.common.ReturnRequestStatus;

/**
 * Response cho yêu cầu trả hàng
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRequestResponse {
    private String id;                      // Mã đơn trả hàng
    private String orderId;                 // Mã đơn hàng
    private ReturnRequestStatus status;     // Trạng thái
    private String reason;                  // Lý do
    private String phoneNumber;             // Số điện thoại
    private String address;                 // Địa chỉ
    private LocalDateTime createdAt;        // Ngày gửi yêu cầu
    private ProductReturnResponse product; // Sản phẩm
}

