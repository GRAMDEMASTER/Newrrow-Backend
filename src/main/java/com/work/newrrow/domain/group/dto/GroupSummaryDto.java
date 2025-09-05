package com.work.newrrow.domain.group.dto;

public record GroupSummaryDto(
        String id, String name, String description, String code,
        String duration, String endDate,
        int memberCount, int totalPoints,
        int targetPoints, boolean showTargetProgress,
        String icon, boolean isActive
) {}