package com.work.newrrow.domain.group.dto;

import java.util.List;

public record GroupDetailDto(
        String id, String name, String description, String code,
        Leader leader, List<Member> members,
        int totalPoints, int targetPoints, boolean showTargetProgress,
        String duration, String endDate
) {
    public record Leader(String id, String name, String role) {}
    public record Member(String id, String name, String email, String role, int points, int completedQuests, boolean isActive) {}
}