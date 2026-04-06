package com.fitfatlab.fitfatlab_backend.modules.diet.service;

import com.fitfatlab.fitfatlab_backend.modules.diet.dto.DietDailySummary;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.DietEntryRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.DietEntryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DietService {
    DietEntryResponse logEntry(UUID userId, DietEntryRequest request);

    DietEntryResponse findEntry(UUID userId, UUID entryId);

    List<DietEntryResponse> findEntries(UUID userId, LocalDate start, LocalDate end);

    DietEntryResponse updateEntry(UUID userId, UUID entryId, DietEntryRequest request);

    void deleteEntry(UUID userId, UUID entryId);

    DietDailySummary getDailySummary(UUID userId, LocalDate date);
}
