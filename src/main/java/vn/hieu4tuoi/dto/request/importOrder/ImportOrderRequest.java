package vn.hieu4tuoi.dto.request.importOrder;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportOrderRequest {
    private String supplierId;
    private List<ImportColorVersionRequest> importColorVersionList;
}
