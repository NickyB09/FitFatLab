package com.fitfatlab.fitfatlab_backend.modules.schedule.service;

import com.fitfatlab.fitfatlab_backend.modules.schedule.dto.CreateTrainingSlotRequest;
import com.fitfatlab.fitfatlab_backend.modules.schedule.dto.TrainingSlotResponse;

import java.util.List;
import java.util.UUID;

public interface TrainingSlotService {
    TrainingSlotResponse createSlot(UUID coachId, CreateTrainingSlotRequest request);
    List<TrainingSlotResponse> findCoachSlots(UUID coachId);
    List<TrainingSlotResponse> findStudentSlots(UUID studentId);
}
