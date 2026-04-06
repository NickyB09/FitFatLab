package com.fitfatlab.fitfatlab_backend.modules.progress.repository;

import com.fitfatlab.fitfatlab_backend.modules.progress.model.ProgressRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgressRepository extends JpaRepository<ProgressRecord, UUID> {

    Optional<ProgressRecord> findByUserIdAndRecordDate(UUID userId, LocalDate date);

    Optional<ProgressRecord> findByIdAndUserId(UUID id, UUID userId);

    List<ProgressRecord> findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(
            UUID userId, LocalDate start, LocalDate end);
}
