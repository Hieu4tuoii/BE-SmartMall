package vn.hieu4tuoi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.hieu4tuoi.common.ProductItemStatus;
import vn.hieu4tuoi.model.ProductItem;

import java.util.List;

public interface ProductItemRepository extends JpaRepository<ProductItem, String> {
    List<ProductItem> findByImportOrderIdAndIsDeletedFalse(String importOrderId);

    //chỉ lấy ds product item chưa bị xóa
    List<ProductItem> findAllByIdInAndIsDeleted(List<String> ids, boolean isDeleted);

    //lấy ds product item theo ids, bao gồm cả bản ghi đã bị xóa (dùng cho join, lịch sử đơn hàng...)
    List<ProductItem> findAllByIdIn(List<String> ids);
    
    ProductItem findByIdAndIsDeleted(String id, boolean isDeleted);
    @Query("SELECT p FROM ProductItem p WHERE p.imeiOrSerial IN :imeiOrSerials and (:status is null or p.status = :status) AND p.isDeleted = :isDeleted")
    List<ProductItem> findAllByImeiOrSerialInAndStatusAndIsDeleted(@Param("imeiOrSerials") List<String> imeiOrSerials, @Param("status") ProductItemStatus status, @Param("isDeleted") boolean isDeleted);

    //ds product item của sản product version color
    @Query("SELECT p FROM ProductItem p WHERE p.productColorVersionId = :productColorVersionId AND (:status is null or p.status = :status) AND (:imeiOrSerial is null or p.imeiOrSerial like :imeiOrSerial) AND p.isDeleted = :isDeleted")
    Page<ProductItem> findByProductColorVersionIdAndStatusAndImeiOrSerial(@Param("productColorVersionId") String productColorVersionId, @Param("status") ProductItemStatus status, @Param("imeiOrSerial") String imeiOrSerial, @Param("isDeleted") boolean isDeleted, Pageable pageable);
}
