package com.umc.movie.controller;

import com.umc.movie.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "상태 확인", description = "서버 상태 확인 API")
@SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
public class HealthController {

    @Operation(summary = "서버 상태 확인", description = "서버가 요청을 처리할 수 있는지 확인합니다. 현재 설정상 인증이 필요합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "서버 정상"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "토큰 없음 또는 유효하지 않음 / AUTH401_2, AUTH401_3, AUTH401_4")
    })
    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }
}
