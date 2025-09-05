package com.work.newrrow.domain.quest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestCompletionRepository extends JpaRepository<QuestCompletion, Long> {
    Optional<QuestCompletion> findByQuestIdAndUserId(Long questId, Long userId);
    int countByQuestId(Long questId);
}