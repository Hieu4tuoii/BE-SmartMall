package vn.hieu4tuoi.dto.respone.bank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class Transaction {
    private String id;
    private String bank_brand_name;
    private String account_number;
    private String transaction_date;
    private String amount_out;
    private String amount_in;
    private String accumulated;
    private String transaction_content;
    private String reference_number;
    private String code;
    private String sub_account;
    private String bank_account_id;
}
