package com.work.newrrow.domain.user.dto;

public record UserProfileDto(Long id, String username, String name, String email, String role, boolean active) {}
