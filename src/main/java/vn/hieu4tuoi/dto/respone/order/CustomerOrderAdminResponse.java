package vn.hieu4tuoi.dto.respone.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderAdminResponse {
    private String id;
    private String name;
    private String phoneNumber;
}
