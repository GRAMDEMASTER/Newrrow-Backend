package com.work.newrrow.domain.group;

import com.work.newrrow.domain.group.dto.*;
import com.work.newrrow.global.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ApiResponse<List<GroupSummaryDto>> list(@RequestHeader("X-User-Id") Long userId){
        return ApiResponse.ok(groupService.listMyGroups(userId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupCreatedDto>> create(@RequestHeader("X-User-Id") Long userId,
                                                               @RequestBody CreateGroupReq req){
        var created = groupService.create(req, userId);
        return ResponseEntity.status(201).body(ApiResponse.ok(created));
    }

    @PostMapping("/join")
    public ApiResponse<JoinGroupRes> join(@RequestHeader("X-User-Id") Long userId,
                                          @RequestBody Map<String, String> body){
        return ApiResponse.ok(groupService.joinByCode(body.get("code"), userId));
    }

    @GetMapping("/{gid}")
    public ApiResponse<GroupDetailDto> detail(@PathVariable String gid){
        return ApiResponse.ok(groupService.detail(gid));
    }

    @DeleteMapping("/{gid}")
    public ResponseEntity<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable String gid){
        groupService.delete(gid, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{gid}/leave")
    public ResponseEntity<Void> leave(@RequestHeader("X-User-Id") Long userId, @PathVariable String gid){
        groupService.leave(gid, userId);
        return ResponseEntity.noContent().build();
    }
}