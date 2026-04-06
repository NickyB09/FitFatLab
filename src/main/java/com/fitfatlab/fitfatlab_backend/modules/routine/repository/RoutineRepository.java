package com.fitfatlab.fitfatlab_backend.modules.routine.repository;

import com.fitfatlab.fitfatlab_backend.modules.routine.model.Routine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoutineRepository extends JpaRepository<Routine, UUID> {
    Page<Routine> findByUserIdAndActiveTrue(UUID userId, Pageable pageable);
}