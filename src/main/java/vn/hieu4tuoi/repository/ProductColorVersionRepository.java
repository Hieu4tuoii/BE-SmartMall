package vn.hieu4tuoi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import vn.hieu4tuoi.model.ProductColorVersion;

public interface ProductColorVersionRepository extends JpaRepository<ProductColorVersion, String> {
    ProductColorVersion findByIdAndIsDeleted(String id, Boolean isDeleted);
    List<ProductColorVersion> findByProductVersionIdAndIsDeletedOrderByCreatedAtAsc(String productVersionId, Boolean isDeleted);
    List<ProductColorVersion> findByProductVersionIdInAndIsDeletedOrderByCreatedAtAsc(List<String> productVersionIds, Boolean isDeleted);

    //chỉ lấy ds color version chưa bị xóa (dùng cho hiển thị catalog hiện tại)
    List<ProductColorVersion> findAllByIdInAndIsDeleted(List<String> ids, Boolean isDeleted);

    //lấy ds color version theo ids, bao gồm cả bản ghi đã bị xóa (dùng cho join, lịch sử đơn hàng...)
    List<ProductColorVersion> findAllByIdIn(List<String> ids);

    List<ProductColorVersion> findAllByProductVersionIdInAndIsDeletedFalse(List<String> productVersionIds);
}
