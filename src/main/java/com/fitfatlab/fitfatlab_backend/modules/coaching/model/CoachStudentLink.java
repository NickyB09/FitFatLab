package com.fitfatlab.fitfatlab_backend.modules.coaching.model;

import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coach_student_links", indexes = {
        @Index(name = "idx_coach_student_links_coach", columnList = "coach_id"),
        @Index(name = "idx_coach_student_links_student", columnList = "student_id"),
        @Index(name = "idx_coach_student_links_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class CoachStudentLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoachStudentLinkStatus status = CoachStudentLinkStatus.PENDING;

    @Column(name = "allow_student_meal_edits", nullable = false)
    private boolean allowStudentMealEdits = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}
