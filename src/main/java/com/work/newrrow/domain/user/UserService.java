package com.work.newrrow.domain.user;

import com.work.newrrow.domain.user.dto.*;
import com.work.newrrow.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    @Transactional(readOnly = true)
    public List<UserProfileDto> list(){
        return users.findAll().stream().map(UserService::toDto).toList();
    }


    @Transactional(readOnly = true)
    public UserProfileDto get(Long id){
        var u = users.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return toDto(u);
    }


    public UserProfileDto create(CreateUserReq req){
        if (users.existsByUsername(req.username()))
            throw new BusinessException(ErrorCode.ALREADY_COMPLETED, "이미 존재하는 사용자명입니다.");
        if (users.existsByEmail(req.email()))
            throw new BusinessException(ErrorCode.ALREADY_COMPLETED, "이미 존재하는 이메일입니다.");
        var u = User.builder()
                .username(req.username())
                .name(req.name())
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .role(User.Role.MEMBER)
                .isActive(true)
                .build();
        users.save(u);
        return toDto(u);
    }


    public UserProfileDto update(Long id, UpdateUserReq req){
        var u = users.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (req.name()!=null) u.setName(req.name());
        if (req.email()!=null) u.setEmail(req.email());
        if (req.password()!=null) u.setPasswordHash(encoder.encode(req.password()));
        if (req.role()!=null) u.setRole(User.Role.valueOf(req.role()));
        if (req.active()!=null) u.setActive(req.active());
        return toDto(u);
    }


    public void delete(Long id){
        var u = users.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        users.delete(u);
    }


    private static UserProfileDto toDto(User u){
        return new UserProfileDto(u.getId(), u.getUsername(), u.getName(), u.getEmail(), u.getRole().name(), u.isActive());
    }
}