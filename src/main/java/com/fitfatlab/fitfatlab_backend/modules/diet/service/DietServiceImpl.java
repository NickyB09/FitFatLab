package com.fitfatlab.fitfatlab_backend.modules.diet.service;

import com.fitfatlab.fitfatlab_backend.modules.diet.dto.*;
import com.fitfatlab.fitfatlab_backend.modules.diet.model.DietEntry;
import com.fitfatlab.fitfatlab_backend.modules.diet.repository.DietEntryRepository;
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
public class DietServiceImpl implements DietService {

    private final DietEntryRepository dietEntryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DietEntryResponse logEntry(UUID userId, DietEntryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        DietEntry entry = new DietEntry();
        entry.setUser(user);
        applyRequest(entry, request);
        entry.setEntryDate(LocalDate.now());

        return toResponse(dietEntryRepository.save(entry));
    }

    @Override
    @Transactional(readOnly = true)
    public DietEntryResponse findEntry(UUID userId, UUID entryId) {
        return toResponse(getOwnedEntry(userId, entryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietEntryResponse> findEntries(UUID userId, LocalDate start, LocalDate end) {
        return dietEntryRepository.findByUserIdAndEntryDateBetweenOrderByEntryDateAsc(userId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DietEntryResponse updateEntry(UUID userId, UUID entryId, DietEntryRequest request) {
        DietEntry entry = getOwnedEntry(userId, entryId);
        applyRequest(entry, request);
        return toResponse(dietEntryRepository.save(entry));
    }

    @Override
    @Transactional
    public void deleteEntry(UUID userId, UUID entryId) {
        dietEntryRepository.delete(getOwnedEntry(userId, entryId));
    }

    @Override
    @Transactional(readOnly = true)
    public DietDailySummary getDailySummary(UUID userId, LocalDate date) {
        List<DietEntry> entries = dietEntryRepository.findByUserIdAndEntryDate(userId, date);

        int totalCal = entries.stream().mapToInt(DietEntry::getCalories).sum();
        float totalPro = (float) entries.stream().mapToDouble(DietEntry::getProteinG).sum();
        float totalCar = (float) entries.stream().mapToDouble(DietEntry::getCarbsG).sum();
        float totalFat = (float) entries.stream().mapToDouble(DietEntry::getFatG).sum();

        return DietDailySummary.builder()
                .date(date)
                .totalCalories(totalCal)
                .totalProteinG(totalPro)
                .totalCarbsG(totalCar)
                .totalFatG(totalFat)
                .entries(entries.stream().map(this::toResponse).toList())
                .build();
    }

    private DietEntry getOwnedEntry(UUID userId, UUID entryId) {
        return dietEntryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diet entry not found"));
    }

    private void applyRequest(DietEntry entry, DietEntryRequest request) {
        entry.setFoodName(request.getFoodName());
        entry.setCalories(request.getCalories());
        entry.setProteinG(request.getProteinG());
        entry.setCarbsG(request.getCarbsG());
        entry.setFatG(request.getFatG());
    }

    private DietEntryResponse toResponse(DietEntry e) {
        return DietEntryResponse.builder()
                .id(e.getId())
                .foodName(e.getFoodName())
                .calories(e.getCalories())
                .proteinG(e.getProteinG())
                .carbsG(e.getCarbsG())
                .fatG(e.getFatG())
                .entryDate(e.getEntryDate())
                .build();
    }
}
