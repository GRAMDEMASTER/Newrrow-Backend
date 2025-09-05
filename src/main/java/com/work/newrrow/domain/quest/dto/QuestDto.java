package com.work.newrrow.domain.quest.dto;

public record QuestDto(
        String id,
        String title,
        String description,
        String type,
        String status,
        int points,
        String deadline,
        String createdBy,
        String estimatedTime,
        String difficulty
) {}