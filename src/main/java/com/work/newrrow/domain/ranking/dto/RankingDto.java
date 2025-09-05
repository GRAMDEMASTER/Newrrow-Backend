package com.work.newrrow.domain.ranking.dto;

public record RankingDto(
        Member member,
        int rank,
        int pointsThisPeriod,          // 요청된 기간의 포인트(weekly/daily/total)
        int questsCompletedThisPeriod  // 요청된 기간의 완료 개수
) {
    public record Member(String id, String name, int points) {}
}