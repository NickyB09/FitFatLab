package com.fitfatlab.fitfatlab_backend.modules.diet.repository;

import com.fitfatlab.fitfatlab_backend.modules.diet.model.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MealPlanRepository extends JpaRepository<MealPlan, UUID> {
    List<MealPlan> findByStudentIdOrderByStartDateDesc(UUID studentId);
    List<MealPlan> findByCoachIdOrderByStartDateDesc(UUID coachId);
}
