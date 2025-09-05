package com.work.newrrow.domain.quest;

import com.work.newrrow.domain.group.Group;
import com.work.newrrow.domain.group.GroupMember;
import com.work.newrrow.domain.group.GroupMemberRepository;
import com.work.newrrow.domain.group.GroupRepository;
import com.work.newrrow.domain.quest.dto.*;
import com.work.newrrow.domain.user.UserRepository;
import com.work.newrrow.global.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {
    private final GroupRepository groups;
    private final GroupMemberRepository members;
    private final QuestRepository quests;
    private final QuestCompletionRepository completions;
    private final UserRepository users;

    public QuestDto create(String gid, CreateQuestReq req, Long currentUserId){
        var g = groups.findByGid(gid).orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        var creator = users.findById(currentUserId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        // 현재 사용자가 해당 그룹 멤버인지 확인 (아니면 권한 없음)
        members.findByGroupIdAndUserId(g.getId(), creator.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PERMISSION_DENIED));

        String qid = QuestIdGenerator.qid();
        while (quests.findByQid(qid).isPresent()) qid = QuestIdGenerator.qid();

        var q = Quest.builder()
                .qid(qid)
                .group(g)
                .title(req.title())
                .description(req.description())
                .type(Quest.Type.valueOf(req.type()))
                .points(Optional.ofNullable(req.points()).orElse(0))
                .deadline(req.deadline()==null ? null : LocalDateTime.parse(req.deadline()))
                .createdBy(creator)
                .estimatedTime(req.estimatedTime())
                .difficulty(req.difficulty()==null ? null : Quest.Difficulty.valueOf(req.difficulty()))
                .build();
        quests.save(q);

        return toDto(q);
    }

    public QuestDto update(String qid, UpdateQuestReq req, Long currentUserId){
        var q = quests.findByQid(qid).orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_FOUND));
        // 생성자 또는 그룹 리더만 수정하도록 제한하고 싶다면 아래 체크 추가:
        var leaderId = q.getGroup().getLeader().getId();
        if (!leaderId.equals(currentUserId) && !q.getCreatedBy().getId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        if (req.title()!=null) q.setTitle(req.title());
        if (req.description()!=null) q.setDescription(req.description());
        if (req.points()!=null) q.setPoints(req.points());
        if (req.deadline()!=null) q.setDeadline(LocalDateTime.parse(req.deadline()));
        if (req.estimatedTime()!=null) q.setEstimatedTime(req.estimatedTime());
        if (req.difficulty()!=null) q.setDifficulty(Quest.Difficulty.valueOf(req.difficulty()));
        if (req.status()!=null) q.setStatus(Quest.Status.valueOf(req.status()));

        return toDto(q);
    }

    public ToggleRes toggle(String qid, Long userId){
        var q = quests.findByQid(qid).orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_FOUND));
        // 해당 그룹의 멤버인지 체크
        var gm = members.findByGroupIdAndUserId(q.getGroup().getId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERMISSION_DENIED));

        var existing = completions.findByQuestIdAndUserId(q.getId(), userId);
        if (existing.isPresent()){
            // 완료 취소
            completions.delete(existing.get());
            gm.setPoints(Math.max(0, gm.getPoints() - q.getPoints()));
            gm.setCompletedQuests(Math.max(0, gm.getCompletedQuests() - 1));
            return new ToggleRes(false, 0);
        } else {
            // 완료 처리
            var user = gm.getUser();
            completions.save(QuestCompletion.builder()
                    .quest(q).user(user).pointsEarned(q.getPoints()).build());
            gm.setPoints(gm.getPoints() + q.getPoints());
            gm.setCompletedQuests(gm.getCompletedQuests() + 1);
            return new ToggleRes(true, q.getPoints());
        }
    }

    public void delete(String qid, Long currentUserId){
        var q = quests.findByQid(qid).orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_FOUND));
        var leaderId = q.getGroup().getLeader().getId();
        if (!leaderId.equals(currentUserId) && !q.getCreatedBy().getId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        quests.delete(q);
    }

    @Transactional(readOnly = true)
    public java.util.List<QuestDto> listByGroup(String gid){
        var g = groups.findByGid(gid).orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        return quests.findByGroupId(g.getId()).stream().map(this::toDto).toList();
    }

    private QuestDto toDto(Quest q){
        return new QuestDto(
                q.getQid(),
                q.getTitle(),
                q.getDescription(),
                q.getType().name(),
                q.getStatus().name(),
                q.getPoints(),
                q.getDeadline()==null ? null : q.getDeadline().toString(),
                q.getCreatedBy().getId().toString(),
                q.getEstimatedTime(),
                q.getDifficulty()==null ? null : q.getDifficulty().name()
        );
    }
}