package com.work.newrrow.domain.group.infra.ai;

import com.work.newrrow.domain.group.infra.ai.dto.AiGenerateReq;
import com.work.newrrow.domain.group.infra.ai.dto.AiQuestDraftRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FastAiClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${ai.base-url:http://localhost:8000}")
    private String baseUrl;

    @Value("${ai.timeout-ms:4000}")
    private long timeoutMs;

    public List<AiQuestDraftRes> generate(String gid, AiGenerateReq req) {
        // WebClient 인스턴스를 매번 생성
        WebClient aiWebClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();

        // 요청과 응답 처리
        AiQuestDraftRes[] body = aiWebClient.post()
                .uri("/groups/{gid}/quests/ai-generate", gid)
                .bodyValue(req)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    // 클라이언트 오류 시 처리
                    return Mono.error(new RuntimeException("4xx error: " + response.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(), response -> {
                    // 서버 오류 시 처리
                    return Mono.error(new RuntimeException("5xx error: " + response.statusCode()));
                })
                .bodyToMono(AiQuestDraftRes[].class)
                .timeout(Duration.ofMillis(timeoutMs))  // 타임아웃 설정
                .onErrorResume(e -> Mono.error(new RuntimeException("AI service error: " + e.getMessage(), e)))  // 오류 처리
                .block();  // 동기적으로 응답을 기다림

        // 응답이 null일 경우 예외 처리
        return Arrays.asList(Objects.requireNonNull(body, "Empty AI response"));
    }
}
