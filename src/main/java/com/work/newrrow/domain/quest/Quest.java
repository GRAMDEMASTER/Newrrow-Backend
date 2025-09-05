package com.work.newrrow.domain.quest;

import com.work.newrrow.domain.group.Group;
import com.work.newrrow.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quest")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Quest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외부 노출용 ID (e.g., "quest_ab12cd34")
    @Column(nullable = false, unique = true, length = 32)
    private String qid;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12)
    private Type type; // PERSONAL / GROUP

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 12)
    private Status status = Status.ACTIVE;

    @Column(nullable = false)
    private int points;

    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(length = 50)
    private String estimatedTime; // "20분" 등

    @Enumerated(EnumType.STRING) @Column(length = 12)
    private Difficulty difficulty; // EASY / MEDIUM / HARD

    public enum Type { PERSONAL, GROUP }
    public enum Status { ACTIVE, COMPLETED, EXPIRED }
    public enum Difficulty { EASY, MEDIUM, HARD }
}