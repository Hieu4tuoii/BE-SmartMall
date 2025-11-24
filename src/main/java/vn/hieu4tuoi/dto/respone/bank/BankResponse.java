package vn.hieu4tuoi.dto.respone.bank;

import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BankResponse {
    private int status;
    private String error;
    private Map<String, Boolean> messages;
    private List<Transaction> transactions;
}
