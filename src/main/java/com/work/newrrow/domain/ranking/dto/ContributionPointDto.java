package com.work.newrrow.domain.ranking.dto;

public record ContributionPointDto(
        Member member,
        int points,           // 누적 포인트(현재 GroupMember.points)
        int quests,           // 누적 완료 수(현재 GroupMember.completedQuests)
        String period         // 응답의 기준 기간(weekly/daily/total)
) {
    public record Member(String id, String name) {}
}