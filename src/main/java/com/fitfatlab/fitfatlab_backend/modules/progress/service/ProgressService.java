package com.fitfatlab.fitfatlab_backend.modules.progress.service;

import com.fitfatlab.fitfatlab_backend.modules.progress.dto.ProgressRecordRequest;
import com.fitfatlab.fitfatlab_backend.modules.progress.dto.ProgressRecordResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ProgressService {
    ProgressRecordResponse upsertTodayRecord(UUID userId, ProgressRecordRequest request);

    List<ProgressRecordResponse> getHistory(UUID userId, LocalDate start, LocalDate end);

    ProgressRecordResponse getRecord(UUID userId, UUID recordId);

    void deleteRecord(UUID userId, UUID recordId);
}
