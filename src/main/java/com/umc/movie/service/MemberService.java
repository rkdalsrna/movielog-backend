// src/main/java/com/umc/movie/service/MemberService.java
package com.umc.movie.service;

import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.NicknameCheckResponse;
import com.umc.movie.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.SignUpRequest;
import com.umc.movie.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository; //	DB에서 회원을 찾거나 저장
    private final PasswordEncoder passwordEncoder; // 비밀번호를 해시값으로 바꾸기

    @Transactional(readOnly = true)
    public NicknameCheckResponse checkNickname(String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);
        return new NicknameCheckResponse(!exists);
    }

    @Transactional(readOnly = true)
    public EmailCheckResponse checkEmail(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        return new EmailCheckResponse(!exists);
    }

    @Transactional
    public MemberResponse signUp(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.email())) { //이메일 중복 확인
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (memberRepository.existsByNickname(request.nickname())) { //닉네임 중복 확인
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        //비밀번호를 해싱하고 회원 객체 만들기
        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) //비밀번호는 해싱한 값
                .nickname(request.nickname())
                .build();

        Member savedMember = memberRepository.save(member); //DB에 저장

        return new MemberResponse( //비밀번호를 제외한 결과 반환
                savedMember.getMemberId(),
                savedMember.getEmail(),
                savedMember.getNickname()
        );
    }
}
