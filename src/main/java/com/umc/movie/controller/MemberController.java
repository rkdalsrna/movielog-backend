package com.umc.movie.controller;

import com.umc.movie.config.SwaggerConfig;
import com.umc.movie.dto.*;
import com.umc.movie.interceptor.AuthInterceptor;
import com.umc.movie.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원", description = "회원가입 및 회원 정보 관리 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 닉네임으로 회원을 등록합니다. 인증이 필요하지 않습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공 / COMMON201"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패 / COMMON4001"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이메일 또는 닉네임 중복 / COMMON4092")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        MemberResponse response = memberService.signUp(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created("회원가입에 성공했습니다.", response));
    }

    @Operation(
            summary = "닉네임 중복 확인",
            description = "입력한 닉네임이 이미 사용 중인지 확인합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "확인 성공 / COMMON200")
    })
    @GetMapping("/nickname/{nickname}")
    public NicknameCheckResponse checkNickname(
            @Parameter(description = "중복 여부를 확인할 닉네임", example = "무비러버")
            @PathVariable String nickname
    ) {
        return memberService.checkNickname(nickname);
    }

    @Operation(
            summary = "이메일 중복 확인",
            description = "입력한 이메일이 이미 사용 중인지 확인합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "확인 성공 / COMMON200")
    })
    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(
            @Parameter(description = "중복 여부를 확인할 이메일", example = "movie@example.com")
            @PathVariable String email
    ) {
        return memberService.checkEmail(email);
    }

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 회원의 정보를 조회합니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공 / COMMON200"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음 또는 유효하지 않음 / AUTH401_2, AUTH401_3, AUTH401_4"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음 / MEMBER404_1")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(
            @Parameter(hidden = true)
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        MemberInfoResponse response = memberService.getMyInfo(memberId);

        return ResponseEntity.ok(
                ApiResponse.ok("회원 정보를 조회했습니다.", response)
        );
    }

    @Operation(
            summary = "내 정보 수정",
            description = "현재 로그인한 회원의 정보를 수정합니다."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공 / COMMON200"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 실패 / COMMON4001"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음 또는 유효하지 않음 / AUTH401_2, AUTH401_3, AUTH401_4"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음 / MEMBER404_1"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "닉네임 중복 / MEMBER409_1")
    })
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @Parameter(hidden = true)
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody MemberUpdateRequest request
    ) {
        MemberInfoResponse response =
                memberService.updateMyInfo(memberId, request);

        return ResponseEntity.ok(
                ApiResponse.ok("회원 정보를 수정했습니다.", response)
        );
    }
}
