package com.fitfatlab.fitfatlab_backend.modules.routine.repository;

import com.fitfatlab.fitfatlab_backend.modules.routine.model.RoutineTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoutineTemplateRepository extends JpaRepository<RoutineTemplate, UUID> {
    List<RoutineTemplate> findByCoachIdOrderByCreatedAtDesc(UUID coachId);
}
