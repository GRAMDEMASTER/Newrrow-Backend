package com.work.newrrow.domain.quest;

import com.work.newrrow.domain.quest.dto.*;
import com.work.newrrow.global.api.ApiResponse;
import com.work.newrrow.domain.group.infra.ai.FastAiClient;
import com.work.newrrow.domain.group.infra.ai.dto.AiGenerateReq;
import com.work.newrrow.domain.group.infra.ai.dto.AiQuestDraftRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;
    private final FastAiClient aiClient;

    @PostMapping("/groups/{gid}/quests")
    public ResponseEntity<ApiResponse<QuestDto>> create(@RequestHeader("X-User-Id") Long userId,
                                                        @PathVariable String gid,
                                                        @RequestBody CreateQuestReq req){
        var created = questService.create(gid, req, userId);
        return ResponseEntity.status(201).body(ApiResponse.ok(created));
    }

    @GetMapping("/groups/{gid}/quests")
    public ApiResponse<List<QuestDto>> list(@PathVariable String gid){
        return ApiResponse.ok(questService.listByGroup(gid));
    }

    // ★ FastAPI 연동
    @PostMapping("/groups/{gid}/quests/ai-generate")
    public ApiResponse<List<AiQuestDraftRes>> aiGenerate(@PathVariable String gid,
                                                         @RequestBody AiGenerateReq req){
        return ApiResponse.ok(aiClient.generate(gid, req));
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
        if (userId == null) throw new IllegalArgumentException("userId is required (header X-User-Id or body)");
        return ApiResponse.ok(questService.toggle(qid, userId));
    }

    @DeleteMapping("/quests/{qid}")
    public ResponseEntity<Void> delete(@RequestHeader("X-User-Id") Long userId,
                                       @PathVariable String qid){
        questService.delete(qid, userId);
        return ResponseEntity.noContent().build();
    }
}