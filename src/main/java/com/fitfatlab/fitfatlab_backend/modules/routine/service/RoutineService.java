package com.fitfatlab.fitfatlab_backend.modules.routine.service;

import com.fitfatlab.fitfatlab_backend.modules.routine.dto.RoutineRequest;
import com.fitfatlab.fitfatlab_backend.modules.routine.dto.RoutineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RoutineService {
    RoutineResponse create(UUID userId, RoutineRequest request);

    Page<RoutineResponse> findActiveByUser(UUID userId, Pageable pageable);

    RoutineResponse findById(UUID routineId, UUID requestingUserId);

    RoutineResponse update(UUID routineId, UUID userId, RoutineRequest request);

    void deactivate(UUID routineId, UUID userId);
}
