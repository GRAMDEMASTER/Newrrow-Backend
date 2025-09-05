package com.work.newrrow.domain.quest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    Optional<Quest> findByQid(String qid);
    List<Quest> findByGroupId(Long groupId);
}