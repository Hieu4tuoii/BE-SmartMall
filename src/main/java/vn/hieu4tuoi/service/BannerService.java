package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.banner.BannerRequest;
import vn.hieu4tuoi.dto.respone.BannerResponse;

import java.util.List;

public interface BannerService {
    String create(BannerRequest request);
    String update(String id, BannerRequest request);
    void delete(String id);
    List<BannerResponse> findAllWithoutPagination();
}


