package com.fitfatlab.fitfatlab_backend.modules.schedule.service;

import com.fitfatlab.fitfatlab_backend.modules.coaching.model.CoachStudentLinkStatus;
import com.fitfatlab.fitfatlab_backend.modules.coaching.repository.CoachStudentLinkRepository;
import com.fitfatlab.fitfatlab_backend.modules.schedule.dto.CreateTrainingSlotRequest;
import com.fitfatlab.fitfatlab_backend.modules.schedule.dto.TrainingSlotResponse;
import com.fitfatlab.fitfatlab_backend.modules.schedule.model.TrainingSlot;
import com.fitfatlab.fitfatlab_backend.modules.schedule.repository.TrainingSlotRepository;
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
public class TrainingSlotServiceImpl implements TrainingSlotService {

    private final TrainingSlotRepository trainingSlotRepository;
    private final UserRepository userRepository;
    private final CoachStudentLinkRepository coachStudentLinkRepository;

    @Override
    @Transactional
    public TrainingSlotResponse createSlot(UUID coachId, CreateTrainingSlotRequest request) {
        User coach = getCoach(coachId);
        User student = getUser(request.getStudentId());
        assertActiveRelationship(coachId, student.getId());

        TrainingSlot slot = new TrainingSlot();
        slot.setCoach(coach);
        slot.setStudent(student);
        slot.setWeekday(request.getWeekday());
        slot.setStartTime(request.getStartTime());
        slot.setNote(request.getNote());

        return toResponse(trainingSlotRepository.save(slot));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingSlotResponse> findCoachSlots(UUID coachId) {
        return trainingSlotRepository.findByCoachIdOrderByWeekdayAscStartTimeAsc(coachId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingSlotResponse> findStudentSlots(UUID studentId) {
        return trainingSlotRepository.findByStudentIdOrderByWeekdayAscStartTimeAsc(studentId)
                .stream().map(this::toResponse).toList();
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only trainers can manage training slots");
        }
        return coach;
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
    }

    private TrainingSlotResponse toResponse(TrainingSlot slot) {
        return TrainingSlotResponse.builder()
                .id(slot.getId())
                .coachId(slot.getCoach().getId())
                .studentId(slot.getStudent().getId())
                .weekday(slot.getWeekday())
                .startTime(slot.getStartTime())
                .note(slot.getNote())
                .createdAt(slot.getCreatedAt())
                .build();
    }
}
