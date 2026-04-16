package com.fitfatlab.fitfatlab_backend.modules.diet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "meal_plan_meals")
@Getter
@Setter
@NoArgsConstructor
public class MealPlanMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Column(name = "meal_name", nullable = false, length = 120)
    private String mealName;

    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;

    @Column(nullable = false)
    private int calories;

    @Column(name = "protein_g", nullable = false)
    private float proteinG;

    @Column(name = "carbs_g", nullable = false)
    private float carbsG;

    @Column(name = "fat_g", nullable = false)
    private float fatG;
}
