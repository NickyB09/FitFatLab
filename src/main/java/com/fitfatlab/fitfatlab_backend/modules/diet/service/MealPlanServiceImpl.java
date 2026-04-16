package com.fitfatlab.fitfatlab_backend.modules.diet.service;

import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import com.fitfatlab.fitfatlab_backend.modules.coaching.repository.CoachStudentLinkRepository;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.CreateMealPlanRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.MealPlanMealUpdateRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.MealPlanResponse;
import com.fitfatlab.fitfatlab_backend.modules.diet.model.MealPlan;
import com.fitfatlab.fitfatlab_backend.modules.diet.model.MealPlanMeal;
import com.fitfatlab.fitfatlab_backend.modules.diet.repository.MealPlanMealRepository;
import com.fitfatlab.fitfatlab_backend.modules.diet.repository.MealPlanRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final MealPlanMealRepository mealPlanMealRepository;
    private final UserRepository userRepository;
    private final CoachStudentLinkRepository coachStudentLinkRepository;

    @Override
    @Transactional
    public MealPlanResponse createMealPlan(UUID coachId, CreateMealPlanRequest request) {
        User coach = getCoach(coachId);
        User student = getUser(request.getStudentId());
        assertActiveRelationship(coachId, student.getId());

        MealPlan mealPlan = new MealPlan();
        mealPlan.setCoach(coach);
        mealPlan.setStudent(student);
        mealPlan.setName(request.getName());
        mealPlan.setDescription(request.getDescription());
        mealPlan.setPeriodType(request.getPeriodType());
        mealPlan.setStartDate(request.getStartDate());
        mealPlan.setEndDate(request.getEndDate());
        mealPlan.setAllowStudentEdits(request.isAllowStudentEdits());
        mealPlan.getMeals().addAll(request.getMeals().stream().map(item -> toMeal(mealPlan, item)).toList());

        return toResponse(mealPlanRepository.save(mealPlan));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealPlanResponse> findStudentMealPlans(UUID studentId) {
        return mealPlanRepository.findByStudentIdOrderByStartDateDesc(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealPlanResponse> findCoachMealPlans(UUID coachId) {
        return mealPlanRepository.findByCoachIdOrderByStartDateDesc(coachId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MealPlanResponse.MealPlanMealResponse updateMealAsStudent(UUID mealId, UUID studentId, MealPlanMealUpdateRequest request) {
        MealPlanMeal meal = mealPlanMealRepository.findById(mealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal plan meal not found: " + mealId));
        MealPlan mealPlan = meal.getMealPlan();

        if (!mealPlan.getStudent().getId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigned student can edit this meal");
        }
        if (!mealPlan.isAllowStudentEdits()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student meal edits are disabled for this plan");
        }
        boolean allowedByCoach = coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatusAndAllowStudentMealEditsTrue(
                mealPlan.getCoach().getId(), studentId, CoachStudentLinkStatus.ACTIVE);
        if (!allowedByCoach) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coach has not enabled student meal edits");
        }

        meal.setMealName(request.getMealName());
        meal.setCalories(request.getCalories());
        meal.setProteinG(request.getProteinG());
        meal.setCarbsG(request.getCarbsG());
        meal.setFatG(request.getFatG());

        return toMealResponse(mealPlanMealRepository.save(meal));
    }

    private void assertActiveRelationship(UUID coachId, UUID studentId) {
        if (!coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, studentId, CoachStudentLinkStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Active coach-student relationship required");
        }
    }

    private User getCoach(UUID coachId) {
        User coach = getUser(coachId);
        boolean isTrainer = coach.getRoles().stream().anyMatch(role -> role.getName() == Role.RoleName.ROLE_TRAINER);
        if (!isTrainer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only trainers can manage meal plans");
        }
        return coach;
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
    }

    private MealPlanMeal toMeal(MealPlan mealPlan, CreateMealPlanRequest.MealRequest request) {
        MealPlanMeal meal = new MealPlanMeal();
        meal.setMealPlan(mealPlan);
        meal.setMealName(request.getMealName());
        meal.setPlannedDate(request.getPlannedDate());
        meal.setCalories(request.getCalories());
        meal.setProteinG(request.getProteinG());
        meal.setCarbsG(request.getCarbsG());
        meal.setFatG(request.getFatG());
        return meal;
    }

    private MealPlanResponse toResponse(MealPlan mealPlan) {
        return MealPlanResponse.builder()
                .id(mealPlan.getId())
                .coachId(mealPlan.getCoach().getId())
                .studentId(mealPlan.getStudent().getId())
                .name(mealPlan.getName())
                .description(mealPlan.getDescription())
                .periodType(mealPlan.getPeriodType())
                .startDate(mealPlan.getStartDate())
                .endDate(mealPlan.getEndDate())
                .allowStudentEdits(mealPlan.isAllowStudentEdits())
                .createdAt(mealPlan.getCreatedAt())
                .meals(mealPlan.getMeals().stream().map(this::toMealResponse).toList())
                .build();
    }

    private MealPlanResponse.MealPlanMealResponse toMealResponse(MealPlanMeal meal) {
        return MealPlanResponse.MealPlanMealResponse.builder()
                .id(meal.getId())
                .mealName(meal.getMealName())
                .plannedDate(meal.getPlannedDate())
                .calories(meal.getCalories())
                .proteinG(meal.getProteinG())
                .carbsG(meal.getCarbsG())
                .fatG(meal.getFatG())
                .build();
    }
}
