package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    @DisplayName("같은 영화에 평점을 중복 등록하면 409와 RATING409_1 예외를 던지고 저장하지 않는다")
    void createRatingWhenDuplicated() {
        Member member = Member.builder()
                .email("member@umc.com")
                .password("암호화된비밀번호")
                .nickname("영화팬")
                .build();
        CreateRatingRequest request = new CreateRatingRequest(550L, 5, "좋아요");

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 550L)).willReturn(true);

        Throwable thrown = catchThrowable(() -> ratingService.create(1L, request));

        assertThat(thrown).isInstanceOf(ApiException.class);
        ApiException exception = (ApiException) thrown;
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getCode()).isEqualTo("RATING409_1");
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("다른 회원의 평점을 수정하면 403과 RATING403_1 예외를 던지고 저장하지 않는다")
    void updateRatingOwnedByAnotherMember() {
        Member owner = org.mockito.Mockito.mock(Member.class);
        given(owner.getMemberId()).willReturn(2L);

        Rating rating = Rating.builder()
                .member(owner)
                .movieId(550L)
                .score(4)
                .comment("기존 후기")
                .build();
        UpdateRatingRequest request = new UpdateRatingRequest(5, "수정 후기");

        given(ratingRepository.findById(10L)).willReturn(Optional.of(rating));

        Throwable thrown = catchThrowable(() -> ratingService.update(1L, 10L, request));

        assertThat(thrown).isInstanceOf(ApiException.class);
        ApiException exception = (ApiException) thrown;
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(exception.getCode()).isEqualTo("RATING403_1");
        verify(ratingRepository, never()).save(any(Rating.class));
    }
}
