package vn.hieu4tuoi.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.common.ProductItemStatus;
import vn.hieu4tuoi.dto.request.importOrder.ImportColorVersionRequest;
import vn.hieu4tuoi.dto.request.importOrder.ImportOrderRequest;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.model.ImportOrder;
import vn.hieu4tuoi.model.Product;
import vn.hieu4tuoi.model.ProductColorVersion;
import vn.hieu4tuoi.model.ProductItem;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.repository.ImportOrderRepository;
import vn.hieu4tuoi.repository.ProductColorVersionRepository;
import vn.hieu4tuoi.repository.ProductItemRepository;
import vn.hieu4tuoi.repository.ProductRepository;
import vn.hieu4tuoi.repository.ProductVersionRepository;
import vn.hieu4tuoi.service.ImportOrderService;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImportOrderServiceImpl implements ImportOrderService {
    private final ImportOrderRepository importOrderRepository;
    private final ProductItemRepository productItemRepository;
    private final ProductColorVersionRepository productColorVersionRepository;
    private final ProductVersionRepository productVersionRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(ImportOrderRequest request) {
        ImportOrder importOrder = new ImportOrder();
        importOrder.setSupplierId(request.getSupplierId());
        importOrderRepository.save(importOrder);

        //tạo và lưu product item cho mỗi imei or serial
        List<ProductItem> productItems = new ArrayList<>();
        for (ImportColorVersionRequest colorVersionRequest : request.getImportColorVersionList()) {
            int quantityImportForColorVersion = colorVersionRequest.getImeiOrSerialList().size();
            for (String imeiOrSerial : colorVersionRequest.getImeiOrSerialList()) {
                ProductItem productItem = new ProductItem();
                productItem.setImeiOrSerial(imeiOrSerial);
                productItem.setImportPrice(colorVersionRequest.getImportPrice());
                // productItem.setStatus(ProductItemStatus.IN_STOCK);
                productItem.setProductColorVersionId(colorVersionRequest.getId());
                productItem.setImportOrderId(importOrder.getId());
                productItems.add(productItem);
            }
            //tăng số lượng tồn kho cho color version và product
            ProductColorVersion productColorVersion = productColorVersionRepository.findById(colorVersionRequest.getId()).orElseThrow(() -> new ResourceNotFoundException("ProductColorVersion not found"));
            productColorVersion.setTotalStock(productColorVersion.getTotalStock() + quantityImportForColorVersion);
            productColorVersionRepository.save(productColorVersion);

            ProductVersion productVersion = productVersionRepository.findById(productColorVersion.getProductVersionId()).orElseThrow(() -> new ResourceNotFoundException("ProductVersion not found"));
            Product product = productRepository.findById(productVersion.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            product.setTotalStock(product.getTotalStock() + quantityImportForColorVersion);
            productRepository.save(product);
        }
        productItemRepository.saveAll(productItems);
        return importOrder.getId();
    }
}
