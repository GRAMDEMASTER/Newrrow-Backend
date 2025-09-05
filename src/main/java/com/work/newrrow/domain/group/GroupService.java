package com.work.newrrow.domain.group;

import com.work.newrrow.domain.group.dto.*;
import com.work.newrrow.domain.user.User;
import com.work.newrrow.domain.user.UserRepository;
import com.work.newrrow.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {
    private final GroupRepository groups;
    private final GroupMemberRepository members;
    private final UserRepository users;


    // 현재 사용자 기준 내가 속한 그룹 목록
    @Transactional(readOnly = true)
    public List<GroupSummaryDto> listMyGroups(Long currentUserId){
        var my = users.findById(currentUserId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        var myMemberships = members.findByUserId(my.getId());
        List<GroupSummaryDto> out = new ArrayList<>();
        for (var m : myMemberships){
            var g = m.getGroup();
            int memberCount = members.countByGroupId(g.getId());
            int totalPoints = members.findByGroupId(g.getId()).stream().mapToInt(GroupMember::getPoints).sum();
            out.add(new GroupSummaryDto(
                    g.getGid(), g.getName(), g.getDescription(), g.getCode(),
                    g.getDuration().name(), g.getEndDate()==null?null:g.getEndDate().toString(),
                    memberCount, totalPoints,
                    g.getTargetPoints(), g.isShowTargetProgress(),
                    g.getIcon(), g.isActive()
            ));
        }
        return out;
    }

    // 그룹 생성 (리더 = currentUser)
    public GroupCreatedDto create(CreateGroupReq req, Long currentUserId){
        var me = users.findById(currentUserId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));


        String gid = GroupCodeGenerator.gid();
        while (groups.existsByGid(gid)) gid = GroupCodeGenerator.gid();
        String code = GroupCodeGenerator.code6();
        while (groups.existsByCode(code)) code = GroupCodeGenerator.code6();


        var g = Group.builder()
                .gid(gid)
                .name(req.name())
                .description(req.description())
                .code(code)
                .duration(Group.Duration.valueOf(req.duration()))
                .endDate(Optional.ofNullable(req.endDate()).map(LocalDate::parse).orElse(null))
                .targetPoints(Optional.ofNullable(req.targetPoints()).orElse(0))
                .showTargetProgress(Optional.ofNullable(req.showTargetProgress()).orElse(true))
                .icon(req.icon())
                .leader(me)
                .build();
        groups.save(g);


        var gm = GroupMember.builder().group(g).user(me).memberRole(GroupMember.Role.LEADER).build();
        members.save(gm);


        return new GroupCreatedDto(
                g.getGid(), g.getName(), g.getCode(), g.getTargetPoints(),
                g.getDuration().name(), g.getEndDate()==null?null:g.getEndDate().toString(),
                new GroupCreatedDto.LeaderDto(me.getId().toString(), me.getName(), "leader")
        );
    }

    // 초대코드로 참여
    public JoinGroupRes joinByCode(String code, Long currentUserId) {
        var g = groups.findByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_GROUP_CODE));

        // 만료 체크: 단기 그룹(SHORT) && 종료일 존재 && 오늘 기준 과거면 만료
        if (g.getDuration() == Group.Duration.SHORT
                && g.getEndDate() != null
                && g.getEndDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.GROUP_EXPIRED);
        }

        var me = users.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이미 가입했는지 확인
        members.findByGroupIdAndUserId(g.getId(), me.getId()).ifPresent(x -> {
            throw new BusinessException(ErrorCode.ALREADY_COMPLETED, "이미 참여한 그룹입니다.");
        });

        // 가입 처리
        members.save(
                GroupMember.builder()
                        .group(g)
                        .user(me)
                        .memberRole(GroupMember.Role.MEMBER)
                        .build()
        );

        return new JoinGroupRes(new JoinGroupRes.Group(g.getGid(), g.getName()), "member");
    }

    @Transactional(readOnly = true)
    public GroupDetailDto detail(String gid){
        var g = groups.findByGid(gid).orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        var ms = members.findByGroupId(g.getId());
        var leader = new GroupDetailDto.Leader(g.getLeader().getId().toString(), g.getLeader().getName(), "leader");
        var memberDtos = ms.stream().map(m -> new GroupDetailDto.Member(
                m.getUser().getId().toString(), m.getUser().getName(), m.getUser().getEmail(),
                m.getMemberRole().name().toLowerCase(), m.getPoints(), m.getCompletedQuests(), m.isActive()
        )).toList();
        int totalPoints = ms.stream().mapToInt(GroupMember::getPoints).sum();
        return new GroupDetailDto(
                g.getGid(), g.getName(), g.getDescription(), g.getCode(),
                leader, memberDtos,
                totalPoints, g.getTargetPoints(), g.isShowTargetProgress(),
                g.getDuration().name(), g.getEndDate()==null?null:g.getEndDate().toString()
        );
    }

    public void delete(String gid, Long currentUserId){
        var g = groups.findByGid(gid).orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        if (!g.getLeader().getId().equals(currentUserId)) throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        groups.delete(g);
    }


    public void leave(String gid, Long currentUserId){
        var g = groups.findByGid(gid).orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        var gm = members.findByGroupIdAndUserId(g.getId(), currentUserId).orElseThrow(() -> new BusinessException(ErrorCode.PERMISSION_DENIED));
        if (gm.getMemberRole()== GroupMember.Role.LEADER)
            throw new BusinessException(ErrorCode.PERMISSION_DENIED, "리더는 탈퇴 전에 위임이 필요합니다.");
        members.delete(gm);
    }

}