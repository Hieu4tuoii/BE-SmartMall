package vn.hieu4tuoi.service.impl;

import vn.hieu4tuoi.dto.respone.importOder.ImportOrderDetailResponse;
import vn.hieu4tuoi.dto.respone.importOder.ImportOrderItemDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import vn.hieu4tuoi.common.ProductItemStatus;
import vn.hieu4tuoi.dto.request.importOrder.ImportColorVersionRequest;
import vn.hieu4tuoi.dto.request.importOrder.ImportOrderRequest;
import vn.hieu4tuoi.dto.request.importOrder.ProductImportSelectRequest;
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.importOder.ImportOrderResponse;
import vn.hieu4tuoi.dto.respone.importOder.ProductImportSelectResponse;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.model.*;
import vn.hieu4tuoi.repository.*;
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
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(ImportOrderRequest request) {
        ImportOrder importOrder = new ImportOrder();
        importOrder.setSupplierId(request.getSupplierId());


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
//                productItem.setImportOrderId(importOrder.getId());
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

        //set tổng tiền cho phiếu nhập
        Long totalAmount = productItems.stream()
                .mapToLong(item -> item.getImportPrice().longValue())
                .sum();
        importOrder.setTotalImportPrice(totalAmount);
        importOrderRepository.save(importOrder);
        //set importOrderId cho product items và lưu
        for (ProductItem productItem : productItems) {
            productItem.setImportOrderId(importOrder.getId());
        }
        productItemRepository.saveAll(productItems);
        return importOrder.getId();
    }

    @Override
    public PageResponse<List<ImportOrderResponse>> getListImportOrders(int page, int size, String sort) {
        //build pageable
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "modifiedAt"); // Mặc định sắp xếp theo modifiedAt giảm dần
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                order = matcher.group(3).equalsIgnoreCase("asc")
                        ? new Sort.Order(Sort.Direction.ASC, columnName)
                        : new Sort.Order(Sort.Direction.DESC, columnName);
            }
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<ImportOrder> importOrderPage = importOrderRepository.findAllByIsDeletedFalse(pageable);
        //list id nhà cung cấp từ ds phiếu nhập va set vao đúng phiếu nhập
        List<String> supplierIds = importOrderPage.getContent().stream().map(ImportOrder::getSupplierId).toList();
        //get ds nhaf cung cấp
        List<SupplierEntity> supplierEntities = supplierRepository.findByIdInAndIsDeletedFalse(supplierIds);
        //map id và tên nhà cung cấp
        Map<String, String> supplierIdNameMap = supplierEntities.stream()
                .collect(Collectors.toMap(SupplierEntity::getId, SupplierEntity::getName));

        //get ds
        Page<ImportOrderResponse> importOrderResponsePage = importOrderPage.map(importOrder -> {
            ImportOrderResponse response = new ImportOrderResponse();
            response.setId(importOrder.getId());
            response.setCreatedAt(importOrder.getCreatedAt());
            response.setModifiedAt(importOrder.getModifiedAt());
            //set tên nhà cung cấp
            response.setSupplierName(supplierIdNameMap.get(importOrder.getSupplierId()));
            response.setTotalImportPrice(importOrder.getTotalImportPrice());
            return response;
        });
        return PageResponse.<List<ImportOrderResponse>>builder()
                .pageNo(importOrderResponsePage.getNumber())
                .pageSize(importOrderResponsePage.getSize())
                .totalPage(importOrderResponsePage.getTotalPages())
                .items(importOrderResponsePage.getContent())
                .build();
    }

    @Override
    public List<ProductImportSelectResponse> getProductImportSelectList(ProductImportSelectRequest request) {
        //neu level 1 thì lấy ds sản phẩm ko bị xóa
        if (request.getLevel() == 1) {
            List<Product> products = productRepository.findAllByIsDeleted(false);
            return products.stream().map(product -> new ProductImportSelectResponse(product.getId(), product.getName())).toList();
        }
        // neu level 2 thì lấy ds phiên bản sản phẩm của 1 sản phẩm và ko bị xóa
        else if (request.getLevel() == 2) {
            List<ProductVersion> productVersions = productVersionRepository.findByProductIdAndIsDeleted(request.getId(), false);
            return productVersions.stream().map(productVersion -> new ProductImportSelectResponse(productVersion.getId(), productVersion.getName())).toList();
        }
        // neu level 3 thì lấy ds màu sắc của 1 phiên bản sản phẩm và ko bị xóa
        else {
            List<ProductColorVersion> productColorVersions = productColorVersionRepository.findByProductVersionIdAndIsDeleted(request.getId(), false);
            return productColorVersions.stream().map(productColorVersion -> new ProductImportSelectResponse(productColorVersion.getId(), productColorVersion.getColor())).toList();
        }
    }

    @Override
    public ImportOrderDetailResponse getById(String id) {
        // Lấy đơn nhập (chỉ lấy nếu chưa bị xóa)
        ImportOrder importOrder = importOrderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("ImportOrder not found"));

        // Lấy thông tin nhà cung cấp (chỉ lấy nếu chưa bị xóa)
        SupplierEntity supplier = supplierRepository.findByIdAndIsDeleted(importOrder.getSupplierId(), false);
        if (supplier == null) {
            throw new ResourceNotFoundException("Supplier not found");
        }

        // Lấy danh sách product items theo importOrderId (chỉ lấy nếu chưa bị xóa)
        List<ProductItem> productItems = productItemRepository.findByImportOrderIdAndIsDeletedFalse(id);

        // Nhóm product items theo colorId
        Map<String, List<ProductItem>> itemsByColorId = productItems.stream()
                .collect(Collectors.groupingBy(ProductItem::getProductColorVersionId));

        // Tạo danh sách ImportOrderItemDetail
        List<ImportOrderItemDetail> items = new ArrayList<>();
        for (Map.Entry<String, List<ProductItem>> entry : itemsByColorId.entrySet()) {
            String colorId = entry.getKey();
            List<ProductItem> colorItems = entry.getValue();

            // Lấy thông tin color, version, product (chỉ lấy nếu chưa bị xóa)
            ProductColorVersion colorVersion = productColorVersionRepository.findByIdAndIsDeleted(colorId, false);
            if (colorVersion == null) {
                throw new ResourceNotFoundException("ProductColorVersion not found");
            }
            ProductVersion version = productVersionRepository.findByIdAndIsDeleted(colorVersion.getProductVersionId(), false);
            if (version == null) {
                throw new ResourceNotFoundException("ProductVersion not found");
            }
            Product product = productRepository.findByIdAndIsDeleted(version.getProductId(), false);
            if (product == null) {
                throw new ResourceNotFoundException("Product not found");
            }

            // Lấy danh sách IMEI và giá nhập
            List<String> imeiList = colorItems.stream()
                    .map(ProductItem::getImeiOrSerial)
                    .collect(Collectors.toList());
            
            // Giá nhập (lấy từ item đầu tiên vì tất cả items cùng color có cùng giá)
            java.math.BigDecimal importPrice = colorItems.get(0).getImportPrice();

            ImportOrderItemDetail itemDetail = new ImportOrderItemDetail(
                    product.getId(),
                    product.getName(),
                    version.getId(),
                    version.getName(),
                    colorVersion.getId(),
                    colorVersion.getColor(),
                    importPrice,
                    colorItems.size(),
                    imeiList
            );
            items.add(itemDetail);
        }

        return new ImportOrderDetailResponse(
                importOrder.getId(),
                supplier.getId(),
                supplier.getName(),
                importOrder.getTotalImportPrice(),
                importOrder.getCreatedAt(),
                importOrder.getModifiedAt(),
                items
        );
    }
}
