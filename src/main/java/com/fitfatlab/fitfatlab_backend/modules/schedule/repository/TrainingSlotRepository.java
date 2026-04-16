package com.fitfatlab.fitfatlab_backend.modules.schedule.repository;

import com.fitfatlab.fitfatlab_backend.modules.schedule.model.TrainingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrainingSlotRepository extends JpaRepository<TrainingSlot, UUID> {
    List<TrainingSlot> findByStudentIdOrderByWeekdayAscStartTimeAsc(UUID studentId);
    List<TrainingSlot> findByCoachIdOrderByWeekdayAscStartTimeAsc(UUID coachId);
}
