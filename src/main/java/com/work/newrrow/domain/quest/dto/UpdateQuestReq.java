package com.work.newrrow.domain.quest.dto;

public record UpdateQuestReq(
        String title,
        String description,
        Integer points,
        String deadline,
        String estimatedTime,
        String difficulty,
        String status         // "ACTIVE" | "COMPLETED" | "EXPIRED"
) {}