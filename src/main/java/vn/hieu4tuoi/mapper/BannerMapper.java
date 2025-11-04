package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.hieu4tuoi.dto.request.banner.BannerRequest;
import vn.hieu4tuoi.dto.respone.BannerResponse;
import vn.hieu4tuoi.model.Banner;

@Mapper(componentModel = "spring")
public interface BannerMapper {
    BannerResponse toResponse(Banner banner);

    Banner toEntity(BannerRequest request);
}


