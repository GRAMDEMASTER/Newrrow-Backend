package com.work.newrrow.domain.quest;
import com.work.newrrow.domain.quest.dto.*;
import com.work.newrrow.global.api.ApiResponse;
import com.work.newrrow.domain.group.infra.ai.FastAiClient;
import com.work.newrrow.domain.group.infra.ai.dto.AiGenerateReq;
import com.work.newrrow.domain.group.infra.ai.dto.AiQuestDraftRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
public class QuestController {

    private static final Logger logger = LoggerFactory.getLogger(QuestController.class);

    private final QuestService questService;
    private final FastAiClient aiClient;

    @PostMapping("/groups/{gid}/quests")
    public ResponseEntity<ApiResponse<QuestDto>> create(@RequestHeader("X-User-Id") Long userId,
                                                        @PathVariable String gid,
                                                        @RequestBody CreateQuestReq req){
        var created = questService.create(gid, req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @GetMapping("/groups/{gid}/quests")
    public ApiResponse<List<QuestDto>> list(@PathVariable String gid){
        return ApiResponse.ok(questService.listByGroup(gid));
    }

    // ★ FastAPI 연동
    @PostMapping("/groups/{gid}/quests/ai-generate")
    public ResponseEntity<ApiResponse<List<AiQuestDraftRes>>> aiGenerate(@PathVariable String gid,
                                                                         @RequestBody AiGenerateReq req){
        try {
            logger.info("AI 퀘스트 생성 요청: gid = {}, req = {}", gid, req);
            List<AiQuestDraftRes> aiQuests = aiClient.generate(gid, req);
            logger.info("AI 퀘스트 생성 성공: {}", aiQuests);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(aiQuests));
        } catch (Exception e) {
            logger.error("AI 퀘스트 생성 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("AI 생성 실패: " + e.getMessage()));
        }
    }

    @PutMapping("/quests/{qid}")
    public ApiResponse<QuestDto> update(@RequestHeader("X-User-Id") Long userId,
                                        @PathVariable String qid,
                                        @RequestBody UpdateQuestReq req){
        return ApiResponse.ok(questService.update(qid, req, userId));
    }

    @PostMapping("/quests/{qid}/toggle")
    public ApiResponse<ToggleRes> toggle(@RequestHeader(value = "X-User-Id", required = false) Long userIdFromHeader,
                                         @PathVariable String qid,
                                         @RequestBody(required = false) ToggleReq req){
        Long userId = userIdFromHeader;
        if (req != null && req.userId() != null) userId = Long.parseLong(req.userId());
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-User-Id 헤더가 필요합니다.");
        }
        return ApiResponse.ok(questService.toggle(qid, userId));
    }

    @DeleteMapping("/quests/{qid}")
    public ResponseEntity<Void> delete(@RequestHeader("X-User-Id") Long userId,
                                       @PathVariable String qid){
        questService.delete(qid, userId);
        return ResponseEntity.noContent().build();
    }
}