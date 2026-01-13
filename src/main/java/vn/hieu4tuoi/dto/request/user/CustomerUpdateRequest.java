package vn.hieu4tuoi.dto.request.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Request cập nhật thông tin tài khoản cho khách hàng hiện tại
 */
@Getter
@Setter
public class CustomerUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private String address;
}


