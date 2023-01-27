package com.idea5.four_cut_photos_map.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.member.dto.KakaoUserInfoParam;
import com.idea5.four_cut_photos_map.member.dto.response.MemberInfoResp;
import com.idea5.four_cut_photos_map.member.entity.Member;
import com.idea5.four_cut_photos_map.member.service.KakaoService;
import com.idea5.four_cut_photos_map.member.service.MemberService;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.AccessToken;
import com.idea5.four_cut_photos_map.security.jwt.dto.response.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final KakaoService kakaoService;

    /**
     * 카카오 로그인
     * @param code 인가코드
     */
    @PreAuthorize("isAnonymous()")
    @GetMapping("/login/oauth2/kakao")
    public ResponseEntity<RsData> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        log.info("카카오 로그인 콜백 요청");
        log.info("code = " + code);
        // 1. 인가 코드로 토큰 발급 요청
        String kakaoAccessToken = kakaoService.getKakaoAccessToken(code);
        // 2. 토큰으로 사용자 정보 가져오기 요청
        KakaoUserInfoParam kakaoUserInfoParam = kakaoService.getKakaoUserInfo(kakaoAccessToken);
        // 3. 제공받은 사용자 정보로 서비스 회원 여부 확인후 회원가입 처리
        Member member = memberService.getMember(kakaoUserInfoParam);
        // 4. 서비스 로그인
        // jwt accessToken, refreshToken 발급
        Token token = memberService.generateTokens(member);
        // header 에 토큰 담기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", token.getAccessToken());
        headers.set("refreshToken", token.getRefreshToken());
        // body 에 토큰 담기
        RsData<Token> body = new RsData<>(
                true,
                "카카오 로그인 성공, Access Token 발급",
                token
        );
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    /**
     * refreshToken 으로 accessToken 재발급
     * @param bearerToken refreshToken
     * @param memberContext
     */
    @PostMapping("/refresh")
    public ResponseEntity<RsData> refreshToken(
            @RequestHeader("Authorization") String bearerToken,
            @AuthenticationPrincipal MemberContext memberContext
    ) {
        log.info("accessToken 재발급 요청");
        String refreshToken = bearerToken.substring("Bearer ".length());
        AccessToken accessToken = memberService.reissueAccessToken(refreshToken, memberContext.getId(), memberContext.getAuthorities());
        // header 에 토큰 담기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", accessToken.getAccessToken());
        // body 에 토큰 담기
        RsData<AccessToken> body = new RsData<>(
                true,
                "Access Token 재발급 성공",
                accessToken
        );
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    /**
     * 서비스 로그아웃
     * @param bearerToken accessToken
     */
    @GetMapping("/logout/oauth2/kakao")
    public ResponseEntity<RsData> logout(@RequestHeader("Authorization") String bearerToken) {
        // 서비스 로그아웃
        log.info("서비스 로그아웃");
        String accessToken = bearerToken.substring("bearer ".length());
        // redis 에 해당 accessToken 블랙리스트로 저장하기
        memberService.logout(accessToken);
        RsData<Object> body = new RsData<>(
                true,
                "로그아웃 성공",
                null
        );
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

//    // TODO: 카카오와 함께 로그아웃 요청시 state 에 accessToken 값을 넘겨 응답에
//    /**
//     * 카카오와 함께 로그아웃
//     * @param bearerToken accessToken
//     * @return
//     */
//    @GetMapping("/logout/oauth2/kakao")
//    public ResponseEntity<RsData> kakaoLogout(@RequestParam("state") String bearerToken) {
//        // 서비스 로그아웃
//        log.info("카카오와 함께 로그아웃");
//        String accessToken = bearerToken.substring("bearer ".length());
//        // redis 에 해당 accessToken 블랙리스트로 저장하기
//        Long expiration = jwtProvider.getExpiration(accessToken);
//        redisDao.setValues(accessToken, "logout", Duration.ofMillis(expiration));
////        session.invalidate();
//        // body 에 토큰 담기
//        RsData<Object> body = new RsData<>(
//                200,
//                "로그아웃 성공",
//                null
//        );
//        return new ResponseEntity<>(body, HttpStatus.OK);
//    }

    // 회원 기본정보 조회
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public ResponseEntity<RsData> getProfile(@AuthenticationPrincipal MemberContext memberContext) {
        MemberInfoResp memberInfoResp = memberService.getMemberInfo(memberContext.getId());
        RsData<MemberInfoResp> body = new RsData<>(
                true, "회원 정보 조회 성공", memberInfoResp
        );
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
