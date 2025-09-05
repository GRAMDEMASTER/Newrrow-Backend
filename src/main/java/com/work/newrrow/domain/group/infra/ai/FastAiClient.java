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

    private final WebClient aiWebClient;

    @Value("${ai.timeout-ms:4000}")
    private long timeoutMs;

    public List<AiQuestDraftRes> generate(String gid, AiGenerateReq req) {
        AiQuestDraftRes[] body = aiWebClient.post()
                .uri("/groups/{gid}/quests/ai-generate", gid)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AiQuestDraftRes[].class)
                .timeout(Duration.ofMillis(timeoutMs))
                .onErrorResume(e -> Mono.error(new RuntimeException("AI service error: " + e.getMessage(), e)))
                .block();

        return Arrays.asList(Objects.requireNonNull(body));
    }
}