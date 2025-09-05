package com.work.newrrow.domain.group;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;


public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    int countByGroupId(Long groupId);
    List<GroupMember> findByUserId(Long userId);
    List<GroupMember> findByGroupId(Long groupId);
}