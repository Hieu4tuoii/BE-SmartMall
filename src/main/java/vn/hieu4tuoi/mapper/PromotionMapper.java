package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;
import vn.hieu4tuoi.dto.request.promotion.PromotionRequest;
import vn.hieu4tuoi.dto.respone.PromotionResponse;
import vn.hieu4tuoi.model.Promotion;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    PromotionResponse toResponse(Promotion promotion);
    Promotion toEntity(PromotionRequest request);
}
