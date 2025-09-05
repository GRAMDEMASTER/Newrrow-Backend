package com.work.newrrow.domain.quest.dto;

public record CreateQuestReq(
        String title,
        String description,
        String type,          // "PERSONAL" | "GROUP"
        Integer points,
        String deadline,      // ISO-8601 e.g. 2025-01-06T23:59:59
        String estimatedTime, // "20분"
        String difficulty     // "EASY" | "MEDIUM" | "HARD"
) {}