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
import vn.hieu4tuoi.dto.respone.PageResponse;
import vn.hieu4tuoi.dto.respone.review.ReviewResponse;
import vn.hieu4tuoi.exception.UnauthorizedException;
import vn.hieu4tuoi.model.Review;
import vn.hieu4tuoi.model.User;
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
    @Override
    public String createReview(ReviewCreationRequest request) {

        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để đánh giá");
        }
        Review review = reviewMapper.toEntity(request);
        review.setUserId(userId);
        reviewRepository.save(review);
        return review.getId();
    }

    
    @Override   
    public PageResponse<List<ReviewResponse>> findAllByProductVersionId(String productVersionId, int page, int size, String sort) {
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
        return PageResponse.<List<ReviewResponse>>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(reviews.getTotalPages())
                .items(reviewResponses)
                .build();
    }
}
