package com.fitfatlab.fitfatlab_backend.modules.diet.repository;

import com.fitfatlab.fitfatlab_backend.modules.diet.model.MealPlanMeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MealPlanMealRepository extends JpaRepository<MealPlanMeal, UUID> {
}
