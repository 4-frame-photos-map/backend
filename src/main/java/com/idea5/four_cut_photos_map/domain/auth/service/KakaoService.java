package com.idea5.four_cut_photos_map.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea5.four_cut_photos_map.domain.auth.dto.param.KakaoUserInfoParam;
import com.idea5.four_cut_photos_map.domain.auth.dto.response.KakaoTokenResp;
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
 * @See <a href="https://juntcom.tistory.com/141">restTemplate 메서드</a>
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

    /**
     * 인가코드로 토큰 받기
     * @param code 인가코드
     * @return kakao AccessToken
     */
    public KakaoTokenResp getKakaoTokens(String code, String kakaoLoginRedirectURI) throws JsonProcessingException {
        log.info("인가코드로 카카오 토큰 발급 요청");
        String url = "https://kauth.kakao.com/oauth/token";
        // header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", kakaoLoginRedirectURI);
        params.add("code", code);
        // header + body 를 합쳐 request 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        // post 요청, 응답
        KakaoTokenResp kakaoTokenResp = restTemplate.postForObject(
                url,
                request,
                KakaoTokenResp.class);
        log.info(kakaoTokenResp.toString());
        return kakaoTokenResp;
    }

    /**
     * 토큰으로 사용자 정보 가져오기
     * @param kakaoTokenResp kakao 에서 발급한 accessToken
     * @return 사용자 정보
     */
    public KakaoUserInfoParam getKakaoUserInfo(KakaoTokenResp kakaoTokenResp) throws JsonProcessingException {
        log.info("토큰으로 사용자 정보 가져오기 요청");
        String url = "https://kapi.kakao.com/v2/user/me";
        // header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(kakaoTokenResp.getAccessToken());
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

    /**
     * 해당 토큰이 만료되었는지 검사
     * @param accessToken 카카오 access token
     */
    public Boolean isExpiredAccessToken(String accessToken) throws JsonProcessingException {
        log.info("토큰 정보 보기 요청");
        String url = "https://kapi.kakao.com/v1/user/access_token_info";
        // header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity request = new HttpEntity<>(headers);
        // get 요청에 header 를 포함하려면 무조건 exchange() 사용해야함!
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class);
        // 응답 정보 역직렬화
        JsonNode jsonNode = objectMapper.readValue(response.getBody(), JsonNode.class);
        if(response.getStatusCode().equals(HttpStatus.OK)) {
            Integer expiresIn = jsonNode.get("expires_in").asInt();
            log.info("expiresIn=" + expiresIn);
            return false;
        } else if(response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            // 토큰 만료
            log.info("만료된 토큰");
            return true;
        } else {
            // 에러 응답 예외처리
            String msg = jsonNode.get("msg").asText();
            throw new RuntimeException(msg);
        }
    }

    /**
     * 토큰 갱신하기
     * @param refreshToken 카카오 refresh token
     */
    public String refresh(String refreshToken) throws JsonProcessingException {
        log.info("토큰 갱신하기 요청");
        String url = "https://kauth.kakao.com/oauth/token";
        // header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("refresh_token", refreshToken);
        // header + body 를 합쳐 request 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        // post 요청, 응답
        ResponseEntity<String> response = restTemplate.postForEntity(
                url,
                request,
                String.class);
        // 응답 정보 역직렬화
        JsonNode jsonNode = objectMapper.readValue(response.getBody(), JsonNode.class);
        if(response.getStatusCode().equals(HttpStatus.OK)) {
            String accessToken = jsonNode.get("access_token").asText();
            log.info("atk=" + accessToken);
            return accessToken;
        } else {
            // 에러 응답 예외처리
            String msg = jsonNode.get("error").asText();
            throw new RuntimeException(msg);
        }
    }
}
