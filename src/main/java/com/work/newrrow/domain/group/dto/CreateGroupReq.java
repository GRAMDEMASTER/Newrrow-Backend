package com.work.newrrow.domain.group.dto;

public record CreateGroupReq(
        String name,
        String description,
        String duration, // "short" | "long"
        String endDate, // yyyy-MM-dd or null
        Integer targetPoints,
        Boolean showTargetProgress,
        String icon,
        Integer maxMembers // 현재 코드에선 사용하지 않지만 스키마에 맞춰 포함
) {}