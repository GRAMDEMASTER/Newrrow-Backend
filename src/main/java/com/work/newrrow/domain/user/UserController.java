package com.work.newrrow.domain.user;

import com.work.newrrow.domain.user.dto.*;
import com.work.newrrow.global.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping
    public ApiResponse<List<UserProfileDto>> list(){
        return ApiResponse.ok(userService.list());
    }


    @GetMapping("/{id}")
    public ApiResponse<UserProfileDto> get(@PathVariable Long id){
        return ApiResponse.ok(userService.get(id));
    }


    @PostMapping("/user")  // 인증 없이 새 사용자 생성
    public ResponseEntity<ApiResponse<UserProfileDto>> create(@RequestBody CreateUserReq req) {
        var created = userService.create(req);
        return ResponseEntity.status(201).body(ApiResponse.ok(created));
    }



    @PutMapping("/{id}")
    public ApiResponse<UserProfileDto> update(@PathVariable Long id, @RequestBody UpdateUserReq req){
        return ApiResponse.ok(userService.update(id, req));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}