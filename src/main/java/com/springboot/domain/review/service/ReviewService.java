package com.springboot.domain.review.service;

import com.springboot.domain.review.dto.ReviewResponseDto;
import com.springboot.domain.review.entity.Reviews;
import com.springboot.domain.review.entity.ReviewsRepository;
import com.springboot.domain.saMonthlySummary.dto.SaPlaceResponseDto;
import com.springboot.domain.saMonthlySummary.entity.SaMonthlySummary;
import com.springboot.domain.saMonthlySummary.entity.SaMonthlySummaryRepository;
import com.springboot.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.springboot.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final SaMonthlySummaryRepository saRepository;
    private  final ReviewsRepository reviewsRepository;
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewByCategory(long saId ,String code) {

        List<Reviews> reviews = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            reviews.addAll(saRepository.findById(saId+i).get().getReviews());
        }
        if (reviews.isEmpty()) {
            throw new EntityNotFoundException(REVIEW_NOT_FOUND, "해당 장소 감성분석 에 대한 리뷰가 없습니다 : " + saId );
        }
        return getReviewResponseDtos(reviews,code);

    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewByCategoryAndMonth(long saId, String code) {

        List<Reviews> reviews = saRepository.findById(saId).get().getReviews();
        if (reviews.isEmpty()) {
            throw new EntityNotFoundException(REVIEW_NOT_FOUND, "해당 장소, 월에 대한 감성분석 에 대한 리뷰가 없습니다 : " + saId );
        }
        return getReviewResponseDtos(reviews,code);

    }
    private List<ReviewResponseDto> getReviewResponseDtos(List<Reviews> reviews, String code) {

        List<ReviewResponseDto> list;

        switch (code) {
            case "C001":
                list = reviews.stream()
                        .filter(entity->entity.getMood() != 2)
                        .map(entity -> ReviewResponseDto.builder()
                                .entity(entity)
                                .state(entity.getMood())
                                .build())
                        .collect(Collectors.toList());
                break;
            case "C002":
                list = reviews.stream()
                        .filter(entity->entity.getTransport() != 2)
                        .map(entity -> ReviewResponseDto.builder()
                                .entity(entity)
                                .state(entity.getTransport())
                                .build())
                        .collect(Collectors.toList());
                break;
            case "C003":
                list = reviews.stream()
                        .filter(entity->entity.getCongestion() != 2)
                        .map(entity -> ReviewResponseDto.builder()
                                .entity(entity)
                                .state(entity.getCongestion())
                                .build())
                        .collect(Collectors.toList());

                break;
            case "C004":
                list = reviews.stream()
                        .filter(entity->entity.getInfra() != 2)
                        .map(entity -> ReviewResponseDto.builder()
                                .entity(entity)
                                .state(entity.getInfra())
                                .build())
                        .collect(Collectors.toList());

                break;
            default:
                throw new EntityNotFoundException(CATEGORY_NOT_FOUND, "해당 코드의 카테고리가 없습니다 : " + code);
        }

        return list;
    }
}
