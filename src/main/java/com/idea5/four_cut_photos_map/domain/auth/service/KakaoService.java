package com.idea5.four_cut_photos_map.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.member.dto.KakaoTokenParam;
import com.idea5.four_cut_photos_map.domain.member.dto.KakaoUserInfoParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @See <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api">kakao rest api</a>
 */
@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class KakaoService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${oauth2.kakao.client-id}")
    private String clientId;

    @Value("${oauth2.kakao.redirect-uri}")
    private String redirectUri;

    /**
     * 인가코드로 토큰 받기
     * @param code 인가코드
     * @return kakao AccessToken
     */
    public KakaoTokenParam getKakaoAccessToken(String code) throws JsonProcessingException {
        log.info("인가코드로 카카오 토큰 발급 요청");
        String url = "https://kauth.kakao.com/oauth/token";
        // header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        // header + body 를 합쳐 request 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        // post 요청, 응답
        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                request,
                String.class);

        // 응답 정보 역직렬화
        JsonNode jsonNode = objectMapper.readValue(response.getBody(), JsonNode.class);
        String accessToken = jsonNode.get("access_token").asText();
        String refreshToken = jsonNode.get("refresh_token").asText();
        log.info("access-token = " + accessToken);
        log.info("refresh-token = " + refreshToken);
        return new KakaoTokenParam(accessToken, refreshToken);
    }

    /**
     * 토큰으로 사용자 정보 가져오기
     * @param kakaoTokenParam kakao 에서 발급한 accessToken, refreshToken
     * @return 사용자 정보
     */
    public KakaoUserInfoParam getKakaoUserInfo(KakaoTokenParam kakaoTokenParam) throws JsonProcessingException {
        log.info("토큰으로 사용자 정보 가져오기 요청");
        String url = "https://kapi.kakao.com/v2/user/me";
        // header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(kakaoTokenParam.getAccessToken());
        // header + body 를 합쳐 request 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        // post 요청, 응답
        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                request,
                String.class);

        // 응답 정보 역직렬화
        JsonNode jsonNode = objectMapper.readValue(response.getBody(), JsonNode.class);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        return KakaoUserInfoParam.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }

    /**
     * 연결끊기
     * @param accessToken Kakao AccessToken
     */
    public void disconnect(String accessToken) throws JsonProcessingException {
        log.info("연결끊기 요청");
        String url = "https://kapi.kakao.com/v1/user/unlink";
        // header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);
        // header + body 를 합쳐 request 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        // post 요청, 응답
        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                request,
                String.class);
        // 응답 정보 역직렬화
        JsonNode jsonNode = objectMapper.readValue(response.getBody(), JsonNode.class);
        if(response.getStatusCode().equals(HttpStatus.OK)) {
            Long id = jsonNode.get("id").asLong();
            log.info(id.toString());
        } else {
            // 에러 응답 예외처리
            String msg = jsonNode.get("msg").asText();
            throw new RuntimeException(msg);
        }
    }
}
