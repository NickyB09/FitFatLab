package com.fitfatlab.fitfatlab_backend.modules.progress.service;

import com.fitfatlab.fitfatlab_backend.modules.progress.dto.ProgressRecordRequest;
import com.fitfatlab.fitfatlab_backend.modules.progress.dto.ProgressRecordResponse;
import com.fitfatlab.fitfatlab_backend.modules.progress.model.ProgressRecord;
import com.fitfatlab.fitfatlab_backend.modules.progress.repository.ProgressRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProgressRecordResponse upsertTodayRecord(UUID userId, ProgressRecordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        LocalDate today = LocalDate.now();

        // Upsert: actualiza si ya existe, crea si no
        ProgressRecord record = progressRepository
            .findByUserIdAndRecordDate(userId, today)
            .orElseGet(() -> {
                ProgressRecord r = new ProgressRecord();
                r.setUser(user);
                r.setRecordDate(today);
                return r;
            });

        if (request.getWeightKg() != null)    record.setWeightKg(request.getWeightKg());
        if (request.getBodyFatPct() != null)  record.setBodyFatPct(request.getBodyFatPct());

        return toResponse(progressRepository.save(record));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgressRecordResponse> getHistory(UUID userId, LocalDate start, LocalDate end) {
        return progressRepository
            .findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, start, end)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressRecordResponse getRecord(UUID userId, UUID recordId) {
        return toResponse(getOwnedRecord(userId, recordId));
    }

    @Override
    @Transactional
    public void deleteRecord(UUID userId, UUID recordId) {
        progressRepository.delete(getOwnedRecord(userId, recordId));
    }

    private ProgressRecord getOwnedRecord(UUID userId, UUID recordId) {
        return progressRepository.findByIdAndUserId(recordId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Progress record not found"));
    }

    private ProgressRecordResponse toResponse(ProgressRecord r) {
        return ProgressRecordResponse.builder()
            .id(r.getId())
            .weightKg(r.getWeightKg())
            .bodyFatPct(r.getBodyFatPct())
            .recordDate(r.getRecordDate())
            .build();
    }
}
