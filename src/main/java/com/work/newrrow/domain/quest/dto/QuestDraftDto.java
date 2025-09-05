package com.work.newrrow.domain.quest.dto;

public record QuestDraftDto(
        String id,
        String title,
        String description,
        String type,
        int points,
        String estimatedTime,
        String difficulty
) {}