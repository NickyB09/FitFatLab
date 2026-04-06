package com.fitfatlab.fitfatlab_backend.modules.diet.repository;

import com.fitfatlab.fitfatlab_backend.modules.diet.model.DietEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DietEntryRepository extends JpaRepository<DietEntry, UUID> {
    List<DietEntry> findByUserIdAndEntryDate(UUID userId, LocalDate date);

    List<DietEntry> findByUserIdAndEntryDateBetweenOrderByEntryDateAsc(
            UUID userId, LocalDate start, LocalDate end);

    Optional<DietEntry> findByIdAndUserId(UUID id, UUID userId);
}
