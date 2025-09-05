package com.work.newrrow.domain.group.infra.ai.dto;

public record AiQuestDraftRes(
        String id,
        String title,
        String description,
        String type,
        Integer points,
        String estimatedTime,
        String difficulty
) {}