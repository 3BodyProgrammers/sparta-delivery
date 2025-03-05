package com.example.spartadelivery.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

    @NotBlank(message = "별점은 필수값입니다.")
    @Range(min = 1, max = 5, message = "별점은 1에서 5 사이의 값이어야 합니다.")
    private Byte rating;

    @NotBlank(message = "평가는 필수값입니다.")
    private String comments;

}
