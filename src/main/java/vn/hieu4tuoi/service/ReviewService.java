package vn.hieu4tuoi.service;

import java.util.List;

import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.request.review.ReviewCreationRequest;
import vn.hieu4tuoi.dto.respone.review.ReviewResponse;

public interface ReviewService {
    String createReview(ReviewCreationRequest request);
    PageResponse<List<ReviewResponse>> findAllByProductVersionId(String productVersionId, int page, int size, String sort);
}