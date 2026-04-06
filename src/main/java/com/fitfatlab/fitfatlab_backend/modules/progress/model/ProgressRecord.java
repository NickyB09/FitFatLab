package com.fitfatlab.fitfatlab_backend.modules.progress.model;

import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "progress_records", indexes = {
        @Index(name = "idx_progress_user_date", columnList = "user_id, record_date", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class ProgressRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "weight_kg")
    private Float weightKg;

    @Column(name = "body_fat_pct")
    private Float bodyFatPct;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;
}