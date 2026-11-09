// src/main/java/com/umc/movie/repository/MemberRepository.java
package com.umc.movie.repository;

import com.umc.movie.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickname(String nickname); //닉네임 중복 조회
    boolean existsByEmail(String email); //이메일 중복 조회
    Optional<Member> findByEmail(String email);
    boolean existsByNicknameAndMemberIdNot(String nickname, Long memberId);
}
