package com.work.newrrow.domain.ranking;

import com.work.newrrow.domain.group.GroupMember;
import com.work.newrrow.domain.group.GroupMemberRepository;
import com.work.newrrow.domain.group.GroupRepository;
import com.work.newrrow.domain.ranking.dto.*;
import com.work.newrrow.domain.quest.QuestCompletion;
import com.work.newrrow.domain.quest.QuestCompletionRepository;
import com.work.newrrow.global.exception.BusinessException;
import com.work.newrrow.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final GroupRepository groups;
    private final GroupMemberRepository members;
    private final QuestCompletionRepository completions;

    /**
     * period: "weekly" | "daily" | "total" (기본 weekly)
     */
    public List<RankingDto> getRanking(String gid, String period) {
        var g = groups.findByGid(gid).orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        var memList = members.findByGroupId(g.getId());

        // 정렬: 누적 포인트 desc
        var sorted = memList.stream()
                .sorted(Comparator.comparingInt(GroupMember::getPoints).reversed())
                .toList();

        // 기간 포인트/완료수 집계 맵
        var periodAgg = aggregatePeriod(g.getId(), normalizePeriod(period));

        List<RankingDto> out = new ArrayList<>();
        int rank = 1;
        for (var m : sorted) {
            int periodPoints = periodAgg.pointsByUser.getOrDefault(m.getUser().getId(), 0);
            int periodQuests = periodAgg.countByUser.getOrDefault(m.getUser().getId(), 0);

            out.add(new RankingDto(
                    new RankingDto.Member(m.getUser().getId().toString(), m.getUser().getName(), m.getPoints()),
                    rank++,
                    periodPoints,
                    periodQuests
            ));
        }
        return out;
    }

    /**
     * period: "weekly" | "daily" | "total" (기본 weekly)
     */
    public List<ContributionPointDto> getContributions(String gid, String period) {
        var g = groups.findByGid(gid).orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        var memList = members.findByGroupId(g.getId());

        return memList.stream()
                .map(m -> new ContributionPointDto(
                        new ContributionPointDto.Member(m.getUser().getId().toString(), m.getUser().getName()),
                        m.getPoints(),
                        m.getCompletedQuests(),
                        normalizePeriod(period)
                ))
                .toList();
    }

    // ===== 내부 유틸 =====

    private static String normalizePeriod(String period) {
        if (period == null) return "weekly";
        return switch (period.toLowerCase()) {
            case "daily", "total" -> period.toLowerCase();
            default -> "weekly";
        };
    }

    private record PeriodAgg(Map<Long, Integer> pointsByUser, Map<Long, Integer> countByUser) {}

    private PeriodAgg aggregatePeriod(Long groupId, String period) {
        if (period.equals("total")) {
            // total일 땐 기간 집계가 아니라 0으로 둬도 되고, 필요시 completions 전체 집계로 교체 가능
            return new PeriodAgg(Map.of(), Map.of());
        }

        var now = LocalDateTime.now();
        LocalDateTime start, end;

        if (period.equals("daily")) {
            start = now.toLocalDate().atStartOfDay();
            end = start.plusDays(1);
        } else { // weekly: 최근 7일
            end = now;
            start = end.minusDays(7);
        }

        List<QuestCompletion> list = completions
                .findByQuest_Group_IdAndCompletedAtBetween(groupId, start, end);

        Map<Long, Integer> pointsByUser = list.stream().collect(
                Collectors.groupingBy(c -> c.getUser().getId(),
                        Collectors.summingInt(QuestCompletion::getPointsEarned))
        );
        Map<Long, Integer> countByUser = list.stream().collect(
                Collectors.groupingBy(c -> c.getUser().getId(),
                        Collectors.reducing(0, e -> 1, Integer::sum))
        );
        return new PeriodAgg(pointsByUser, countByUser);
    }
}