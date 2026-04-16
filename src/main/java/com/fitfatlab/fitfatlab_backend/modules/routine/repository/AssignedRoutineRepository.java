package com.fitfatlab.fitfatlab_backend.modules.routine.repository;

import com.fitfatlab.fitfatlab_backend.modules.routine.model.AssignedRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssignedRoutineRepository extends JpaRepository<AssignedRoutine, UUID> {
    List<AssignedRoutine> findByStudentIdOrderByScheduledDateDesc(UUID studentId);
    List<AssignedRoutine> findByCoachIdOrderByScheduledDateDesc(UUID coachId);
}
