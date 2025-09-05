package com.work.newrrow.domain.quest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface QuestCompletionRepository extends JpaRepository<QuestCompletion, Long> {
    Optional<QuestCompletion> findByQuestIdAndUserId(Long questId, Long userId);
    int countByQuestId(Long questId);

    List<QuestCompletion> findByQuest_Group_IdAndCompletedAtBetween(Long groupId, LocalDateTime start, LocalDateTime end);
}