package vn.hieu4tuoi.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.hieu4tuoi.common.WarrantyStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Request để cập nhật trạng thái bảo hành
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWarrantyStatusRequest {
    @NotNull(message = "Trạng thái bảo hành không được để trống")
    private WarrantyStatus status;
}

