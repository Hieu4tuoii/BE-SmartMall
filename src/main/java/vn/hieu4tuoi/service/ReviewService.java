package vn.hieu4tuoi.service;

import vn.hieu4tuoi.dto.request.review.ReviewCreationRequest;
import vn.hieu4tuoi.dto.respone.review.ReviewListResponse;

public interface ReviewService {
    String createReview(ReviewCreationRequest request);
    ReviewListResponse findAllByProductVersionId(String productVersionId, int page, int size, String sort);
}