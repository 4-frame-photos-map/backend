package com.idea5.four_cut_photos_map.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.member.dto.KakaoUserInfoParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public String getKakaoAccessToken(String code) throws JsonProcessingException {
        log.info("인가코드로 카카오 토큰 발급 요청");
        String url = "https://kauth.kakao.com/oauth/token";
        String accessToken = "";
        // TODO: refreshToken 은 응답값이 제대로 넘어오는지 확인 용도로만 사용중!
        String refreshToken = "";
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
        accessToken = jsonNode.get("access_token").asText();
        refreshToken = jsonNode.get("refresh_token").asText();
        log.info("access-token = " + accessToken);
        log.info("refresh-token = " + refreshToken);
        return accessToken;
    }

    /**
     * 토큰으로 사용자 정보 가져오기
     * @param accessToken
     * @return 사용자 정보
     */
    public KakaoUserInfoParam getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        log.info("토큰으로 사용자 정보 가져오기 요청");
        String url = "https://kapi.kakao.com/v2/user/me";
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
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        return KakaoUserInfoParam.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }
}
