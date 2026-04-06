package com.fitfatlab.fitfatlab_backend.modules.progress.service;

import com.fitfatlab.fitfatlab_backend.modules.progress.dto.ProgressRecordRequest;
import com.fitfatlab.fitfatlab_backend.modules.progress.dto.ProgressRecordResponse;
import com.fitfatlab.fitfatlab_backend.modules.progress.model.ProgressRecord;
import com.fitfatlab.fitfatlab_backend.modules.progress.repository.ProgressRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceImplTest {

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    @Test
    void upsertTodayRecordShouldCreateNewRecordWhenMissing() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        ProgressRecordRequest request = new ProgressRecordRequest();
        request.setWeightKg(82.5f);
        request.setBodyFatPct(14.2f);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(progressRepository.findByUserIdAndRecordDate(userId, LocalDate.now())).thenReturn(Optional.empty());
        when(progressRepository.save(any(ProgressRecord.class))).thenAnswer(invocation -> {
            ProgressRecord saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        ProgressRecordResponse response = progressService.upsertTodayRecord(userId, request);

        assertThat(response.getWeightKg()).isEqualTo(82.5f);
        assertThat(response.getBodyFatPct()).isEqualTo(14.2f);
        assertThat(response.getRecordDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void getHistoryShouldMapRepositoryResults() {
        UUID userId = UUID.randomUUID();
        LocalDate start = LocalDate.now().minusDays(2);
        LocalDate end = LocalDate.now();

        ProgressRecord record = new ProgressRecord();
        record.setId(UUID.randomUUID());
        record.setWeightKg(80.0f);
        record.setBodyFatPct(13.5f);
        record.setRecordDate(start.plusDays(1));

        when(progressRepository.findByUserIdAndRecordDateBetweenOrderByRecordDateAsc(userId, start, end))
                .thenReturn(List.of(record));

        List<ProgressRecordResponse> history = progressService.getHistory(userId, start, end);

        assertThat(history).hasSize(1);
        assertThat(history.getFirst().getWeightKg()).isEqualTo(80.0f);
        assertThat(history.getFirst().getRecordDate()).isEqualTo(start.plusDays(1));
    }
}
