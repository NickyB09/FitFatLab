package com.fitfatlab.fitfatlab_backend.modules.diet.model;

import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "diet_entries", indexes = {
        @Index(name = "idx_diet_user_date", columnList = "user_id, entry_date")
})
@Getter
@Setter
@NoArgsConstructor
public class DietEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "food_name", nullable = false, length = 200)
    private String foodName;

    @Column(nullable = false)
    private int calories;

    @Column(name = "protein_g", nullable = false)
    private float proteinG;

    @Column(name = "carbs_g", nullable = false)
    private float carbsG;

    @Column(name = "fat_g", nullable = false)
    private float fatG;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;
}