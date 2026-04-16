package com.fitfatlab.fitfatlab_backend.modules.diet.service;

import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import com.fitfatlab.fitfatlab_backend.modules.coaching.repository.CoachStudentLinkRepository;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.CreateMealPlanRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.MealPlanMealUpdateRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.model.MealPlan;
import com.fitfatlab.fitfatlab_backend.modules.diet.model.MealPlanMeal;
import com.fitfatlab.fitfatlab_backend.modules.diet.model.MealPlanPeriodType;
import com.fitfatlab.fitfatlab_backend.modules.diet.repository.MealPlanMealRepository;
import com.fitfatlab.fitfatlab_backend.modules.diet.repository.MealPlanRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.Role;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MealPlanServiceImplTest {

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private MealPlanMealRepository mealPlanMealRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CoachStudentLinkRepository coachStudentLinkRepository;

    @InjectMocks
    private MealPlanServiceImpl mealPlanService;

    @Test
    void shouldCreateWeeklyMealPlanForActiveCoachStudentRelationship() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        User coach = userWithRole(coachId, Role.RoleName.ROLE_TRAINER);
        User student = userWithRole(studentId, Role.RoleName.ROLE_USER);
        CreateMealPlanRequest request = weeklyMealPlanRequest(studentId);

        when(userRepository.findById(coachId)).thenReturn(Optional.of(coach));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, studentId, CoachStudentLinkStatus.ACTIVE))
                .thenReturn(true);
        when(mealPlanRepository.save(any(MealPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = mealPlanService.createMealPlan(coachId, request);

        assertThat(response.getStudentId()).isEqualTo(studentId);
        assertThat(response.getPeriodType()).isEqualTo(MealPlanPeriodType.WEEK);
        assertThat(response.isAllowStudentEdits()).isTrue();
        assertThat(response.getMeals()).hasSize(2);
    }

    @Test
    void shouldRejectMealPlanCreationWithoutActiveRelationship() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        User coach = userWithRole(coachId, Role.RoleName.ROLE_TRAINER);
        User student = userWithRole(studentId, Role.RoleName.ROLE_USER);
        CreateMealPlanRequest request = weeklyMealPlanRequest(studentId);

        when(userRepository.findById(coachId)).thenReturn(Optional.of(coach));
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatus(coachId, studentId, CoachStudentLinkStatus.ACTIVE))
                .thenReturn(false);

        assertThatThrownBy(() -> mealPlanService.createMealPlan(coachId, request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldAllowStudentToEditMealWhenCoachPermissionIsEnabled() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID mealId = UUID.randomUUID();
        MealPlanMeal meal = mealPlanMeal(mealId, coachId, studentId, true);
        MealPlanMealUpdateRequest request = new MealPlanMealUpdateRequest();
        request.setMealName("Edited Breakfast");
        request.setCalories(520);
        request.setProteinG(35);
        request.setCarbsG(50);
        request.setFatG(14);

        when(mealPlanMealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        when(coachStudentLinkRepository.existsByCoachIdAndStudentIdAndStatusAndAllowStudentMealEditsTrue(
                coachId, studentId, CoachStudentLinkStatus.ACTIVE)).thenReturn(true);
        when(mealPlanMealRepository.save(meal)).thenReturn(meal);

        var response = mealPlanService.updateMealAsStudent(mealId, studentId, request);

        assertThat(response.getMealName()).isEqualTo("Edited Breakfast");
        assertThat(response.getCalories()).isEqualTo(520);
    }

    @Test
    void shouldRejectStudentMealEditWhenPermissionIsDisabled() {
        UUID coachId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID mealId = UUID.randomUUID();
        MealPlanMeal meal = mealPlanMeal(mealId, coachId, studentId, false);
        MealPlanMealUpdateRequest request = new MealPlanMealUpdateRequest();
        request.setMealName("Edited Breakfast");
        request.setCalories(520);
        request.setProteinG(35);
        request.setCarbsG(50);
        request.setFatG(14);

        when(mealPlanMealRepository.findById(mealId)).thenReturn(Optional.of(meal));

        assertThatThrownBy(() -> mealPlanService.updateMealAsStudent(mealId, studentId, request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    private CreateMealPlanRequest weeklyMealPlanRequest(UUID studentId) {
        CreateMealPlanRequest request = new CreateMealPlanRequest();
        request.setStudentId(studentId);
        request.setName("Week 1 Nutrition");
        request.setDescription("High protein week");
        request.setPeriodType(MealPlanPeriodType.WEEK);
        request.setStartDate(LocalDate.of(2026, 4, 20));
        request.setEndDate(LocalDate.of(2026, 4, 26));
        request.setAllowStudentEdits(true);

        CreateMealPlanRequest.MealRequest breakfast = new CreateMealPlanRequest.MealRequest();
        breakfast.setMealName("Breakfast");
        breakfast.setPlannedDate(LocalDate.of(2026, 4, 20));
        breakfast.setCalories(500);
        breakfast.setProteinG(30);
        breakfast.setCarbsG(55);
        breakfast.setFatG(12);

        CreateMealPlanRequest.MealRequest lunch = new CreateMealPlanRequest.MealRequest();
        lunch.setMealName("Lunch");
        lunch.setPlannedDate(LocalDate.of(2026, 4, 20));
        lunch.setCalories(700);
        lunch.setProteinG(45);
        lunch.setCarbsG(65);
        lunch.setFatG(20);

        request.setMeals(List.of(breakfast, lunch));
        return request;
    }

    private MealPlanMeal mealPlanMeal(UUID mealId, UUID coachId, UUID studentId, boolean allowStudentEdits) {
        MealPlan plan = new MealPlan();
        plan.setId(UUID.randomUUID());
        plan.setCoach(userWithRole(coachId, Role.RoleName.ROLE_TRAINER));
        plan.setStudent(userWithRole(studentId, Role.RoleName.ROLE_USER));
        plan.setAllowStudentEdits(allowStudentEdits);
        plan.setPeriodType(MealPlanPeriodType.DAY);
        plan.setStartDate(LocalDate.of(2026, 4, 20));
        plan.setEndDate(LocalDate.of(2026, 4, 20));

        MealPlanMeal meal = new MealPlanMeal();
        meal.setId(mealId);
        meal.setMealPlan(plan);
        meal.setMealName("Breakfast");
        meal.setPlannedDate(LocalDate.of(2026, 4, 20));
        meal.setCalories(480);
        meal.setProteinG(32);
        meal.setCarbsG(48);
        meal.setFatG(12);
        return meal;
    }

    private User userWithRole(UUID id, Role.RoleName roleName) {
        Role role = new Role();
        role.setName(roleName);

        User user = new User();
        user.setId(id);
        user.setEmail(id + "@mail.com");
        user.setPasswordHash("encoded");
        user.setFullName("Test User");
        user.setEnabled(true);
        user.setRoles(Set.of(role));
        return user;
    }
}
