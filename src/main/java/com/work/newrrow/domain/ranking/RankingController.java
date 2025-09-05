package com.work.newrrow.domain.ranking;

import com.work.newrrow.domain.ranking.dto.*;
import com.work.newrrow.global.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{gid}")
public class RankingController {

    private final RankingService rankingService;

    // 랭킹 (기본 weekly) — ?period=weekly|daily|total
    @GetMapping("/ranking")
    public ApiResponse<List<RankingDto>> ranking(@PathVariable String gid,
                                                 @RequestParam(value = "period", required = false) String period) {
        return ApiResponse.ok(rankingService.getRanking(gid, period));
    }

    // 기여도 차트 데이터 (기본 weekly) — 실제 값은 누적 points/quests, 응답에 period 표시
    @GetMapping("/contributions/chart")
    public ApiResponse<List<ContributionPointDto>> contributions(@PathVariable String gid,
                                                                 @RequestParam(value = "period", required = false) String period) {
        return ApiResponse.ok(rankingService.getContributions(gid, period));
    }
}