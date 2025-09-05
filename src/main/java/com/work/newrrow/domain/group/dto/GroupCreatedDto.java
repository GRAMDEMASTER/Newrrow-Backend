package com.work.newrrow.domain.group.dto;

public record GroupCreatedDto(
        String id, String name, String code,
        int targetPoints, String duration, String endDate,
        LeaderDto leader
) {
    public record LeaderDto(String id, String name, String role) {}
}