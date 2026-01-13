package vn.hieu4tuoi.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.ReturnRequestStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Request để cập nhật trạng thái trả hàng
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReturnRequestStatusRequest {
    @NotNull(message = "Trạng thái trả hàng không được để trống")
    private ReturnRequestStatus status;
}

