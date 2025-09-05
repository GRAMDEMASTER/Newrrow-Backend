package com.work.newrrow.domain.group.dto;

public record JoinGroupRes( Group group, String memberRole ) {
    public record Group(String id, String name) {}
}