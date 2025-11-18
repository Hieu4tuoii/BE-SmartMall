package vn.hieu4tuoi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.hieu4tuoi.dto.request.review.ReviewCreationRequest;
import vn.hieu4tuoi.dto.respone.ResponseData;
import vn.hieu4tuoi.service.ReviewService;

@RestController
@RequestMapping("/review")
@Tag(name = "Review Controller")
@RequiredArgsConstructor
@Validated
public class ReviewController {
    private final ReviewService reviewService;

    
    @PostMapping("/public/create")
    public ResponseData<?> create(@RequestBody @Valid ReviewCreationRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Đánh giá thành công", reviewService.createReview(request));
    }

    @GetMapping("/public/list/{productVersionId}")
    public ResponseData<?> findAllByProductVersionId(@PathVariable String productVersionId, @RequestParam int page, @RequestParam int size, @RequestParam String sort) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lấy danh sách đánh giá thành công", reviewService.findAllByProductVersionId(productVersionId, page, size, sort));
    }
}
