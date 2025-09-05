package com.work.newrrow.domain.group;

import com.work.newrrow.domain.user.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "group_members",
        uniqueConstraints = @UniqueConstraint(name = "uq_group_user", columnNames = {"group_id", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "group_id", nullable = false)
    private Group group;


    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role memberRole = Role.MEMBER;


    @Column(nullable = false)
    private int points = 0;


    @Column(nullable = false)
    private int completedQuests = 0;


    @Column(nullable = false)
    private boolean isActive = true;


    public enum Role { LEADER, MEMBER }
}