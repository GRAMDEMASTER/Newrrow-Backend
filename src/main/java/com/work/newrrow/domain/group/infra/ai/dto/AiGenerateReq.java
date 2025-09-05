package com.work.newrrow.domain.group.infra.ai.dto;

public record AiGenerateReq(
        String type,      // "PERSONAL" | "GROUP"
        String theme,
        Integer count,
        String difficulty  // "EASY" | "MEDIUM" | "HARD"
) {}