package com.idea5.four_cut_photos_map.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.idea5.four_cut_photos_map.global.util.Util;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.member.dto.response.KakaoUserInfoDto;
import com.idea5.four_cut_photos_map.member.entity.Member;
import com.idea5.four_cut_photos_map.member.service.KakaoService;
import com.idea5.four_cut_photos_map.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    @GetMapping("/login/oauth2/kakao")
    public ResponseEntity<RsData> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        log.info("카카오 로그인 콜백 요청");
        log.info("code = " + code);
        // 1. 인가 코드로 토큰 발급 요청
        String kakaoAccessToken = kakaoService.getKakaoAccessToken(code);
        // 2. 토큰으로 사용자 정보 가져오기 요청
        KakaoUserInfoDto kakaoUserInfoDto = kakaoService.getKakaoUserInfo(kakaoAccessToken);
        // 3. 제공받은 사용자 정보로 서비스 회원 여부 확인후 회원가입 처리
        Member member = memberService.getMember(kakaoUserInfoDto);
        // 4. 서비스 로그인
        // jwt accessToken 발급
        String accessToken = memberService.getAccessToken(member);
        // header 에 토큰 담기
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", accessToken);
        // body 에 토큰 담기
        RsData<Map<String, Object>> body = new RsData<>(
                200,
                "카카오 로그인 성공, Access Token 발급",
                Util.mapOf(
                        "accessToken", accessToken
                )
        );
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
