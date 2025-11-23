package vn.hieu4tuoi.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.Security.SecurityUtils;
import vn.hieu4tuoi.common.CommonUtils;
import vn.hieu4tuoi.dto.request.review.ReviewCreationRequest;
import vn.hieu4tuoi.dto.respone.review.ReviewListResponse;
import vn.hieu4tuoi.dto.respone.review.ReviewResponse;
import vn.hieu4tuoi.dto.respone.review.ReviewStatistics;
import vn.hieu4tuoi.exception.ResourceNotFoundException;
import vn.hieu4tuoi.exception.UnauthorizedException;
import vn.hieu4tuoi.model.ProductVersion;
import vn.hieu4tuoi.model.Review;
import vn.hieu4tuoi.model.User;
import vn.hieu4tuoi.repository.ProductVersionRepository;
import vn.hieu4tuoi.repository.ReviewRepository;
import vn.hieu4tuoi.repository.UserRepository;
import vn.hieu4tuoi.service.ReviewService;
import vn.hieu4tuoi.mapper.ReviewMapper;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final ProductVersionRepository productVersionRepository;
    @Override
    public String createReview(ReviewCreationRequest request) {

        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để đánh giá");
        }
        Review review = reviewMapper.toEntity(request);
        review.setUserId(userId);
        reviewRepository.save(review);

        updateProductVersionRating(request.getProductVersionId());
        return review.getId();
    }

    private void updateProductVersionRating(String productVersionId) {
        ProductVersion productVersion = productVersionRepository.findByIdAndIsDeleted(productVersionId, false);
        if (productVersion == null) {
            throw new ResourceNotFoundException("Không tìm thấy phiên bản sản phẩm");
        }
        Double averageRating = reviewRepository.getAverageRatingByProductVersionIdAndIsDeleted(productVersionId, false);
        if (averageRating == null) {
            averageRating = 0.0;
        }
        productVersion.setAverageRating(averageRating);
        productVersion.setTotalRating( reviewRepository.countByProductVersionIdAndIsDeleted(productVersionId, false));
        productVersionRepository.save(productVersion);
    }
    
    @Override   
    public ReviewListResponse findAllByProductVersionId(String productVersionId, int page, int size, String sort) {
        Pageable pageable = CommonUtils.createPageable(page, size, sort);
        Page<Review> reviews = reviewRepository.findAllByProductVersionIdAndIsDeleted(productVersionId, false, pageable);

        //list user id
        List<String> userIds = reviews.stream().map(Review::getUserId).toList();
        List<User> users = userRepository.findAllByIdInAndIsDeleted(userIds, false);
        Map<String, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<ReviewResponse> reviewResponses = reviews.stream().map(review -> {
            ReviewResponse response = reviewMapper.toResponse(review);
            response.setUserFullName(userMap.get(review.getUserId()).getFullName());
            return response;
        }).toList();
        
        // Tính toán thống kê đánh giá
        ReviewStatistics statistics = calculateReviewStatistics(productVersionId);
        
        return ReviewListResponse.builder()
                .reviews(reviewResponses)
                .statistics(statistics)
                .pageNo(page)
                .pageSize(size)
                .totalPage(reviews.getTotalPages())
                .build();
    }
    
    /**
     * Tính toán thống kê đánh giá cho một sản phẩm
     * Bao gồm: tổng số đánh giá, đánh giá trung bình, số lượng đánh giá từng mức 1-5 sao
     */
    private ReviewStatistics calculateReviewStatistics(String productVersionId) {
        // Lấy tổng số đánh giá
        Integer totalReviews = reviewRepository.countByProductVersionIdAndIsDeleted(productVersionId, false);
        
        // Lấy đánh giá trung bình
        Double averageRating = reviewRepository.getAverageRatingByProductVersionIdAndIsDeleted(productVersionId, false);
        if (averageRating == null) {
            averageRating = 0.0;
        }
        
        // Đếm số lượng đánh giá cho từng mức rating (1-5 sao)
        Integer rating1Count = reviewRepository.countByProductVersionIdAndRatingAndIsDeleted(productVersionId, 1, false);
        Integer rating2Count = reviewRepository.countByProductVersionIdAndRatingAndIsDeleted(productVersionId, 2, false);
        Integer rating3Count = reviewRepository.countByProductVersionIdAndRatingAndIsDeleted(productVersionId, 3, false);
        Integer rating4Count = reviewRepository.countByProductVersionIdAndRatingAndIsDeleted(productVersionId, 4, false);
        Integer rating5Count = reviewRepository.countByProductVersionIdAndRatingAndIsDeleted(productVersionId, 5, false);
        
        return ReviewStatistics.builder()
                .totalReviews(totalReviews)
                .averageRating(averageRating)
                .rating1Count(rating1Count != null ? rating1Count : 0)
                .rating2Count(rating2Count != null ? rating2Count : 0)
                .rating3Count(rating3Count != null ? rating3Count : 0)
                .rating4Count(rating4Count != null ? rating4Count : 0)
                .rating5Count(rating5Count != null ? rating5Count : 0)
                .build();
    }
}
