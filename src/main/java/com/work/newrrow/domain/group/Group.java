package com.work.newrrow.domain.group;

import com.work.newrrow.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Entity
@Table(name = "group")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Group {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외부 노출용 ID (예: "group_abc123")
    @Column(nullable = false, unique = true, length = 32)
    private String gid;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // 6자리 입장 코드
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Duration duration; // short / long

    private LocalDate endDate; // long일 경우 null

    @Column(nullable = false)
    private int targetPoints;

    @Column(nullable = false)
    private boolean showTargetProgress = true;

    @Column(length = 10)
    private String icon;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;


    public enum Duration {
        SHORT,
        LONG
    }
}