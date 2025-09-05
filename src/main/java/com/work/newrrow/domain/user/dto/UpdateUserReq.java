package com.work.newrrow.domain.user.dto;

public record UpdateUserReq(String name, String email, String password, String role, Boolean active) {}