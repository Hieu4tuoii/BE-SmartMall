package vn.hieu4tuoi.mapper;

import org.mapstruct.Mapper;
import vn.hieu4tuoi.dto.request.review.ReviewCreationRequest;
import vn.hieu4tuoi.dto.respone.review.ReviewResponse;
import vn.hieu4tuoi.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review toEntity(ReviewCreationRequest request);
    ReviewResponse toResponse(Review review);
}