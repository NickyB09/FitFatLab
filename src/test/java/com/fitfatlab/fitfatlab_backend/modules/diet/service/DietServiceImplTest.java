package com.fitfatlab.fitfatlab_backend.modules.diet.service;

import com.fitfatlab.fitfatlab_backend.modules.diet.dto.DietEntryRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.DietEntryResponse;
import com.fitfatlab.fitfatlab_backend.modules.diet.model.DietEntry;
import com.fitfatlab.fitfatlab_backend.modules.diet.repository.DietEntryRepository;
import com.fitfatlab.fitfatlab_backend.modules.user.model.User;
import com.fitfatlab.fitfatlab_backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DietServiceImplTest {

    @Mock
    private DietEntryRepository dietEntryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DietServiceImpl dietService;

    @Test
    void updateEntryShouldModifyOwnedEntry() {
        UUID userId = UUID.randomUUID();
        UUID entryId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        DietEntry entry = new DietEntry();
        entry.setId(entryId);
        entry.setUser(user);
        entry.setEntryDate(LocalDate.now());

        DietEntryRequest request = new DietEntryRequest();
        request.setFoodName("Chicken and rice");
        request.setCalories(650);
        request.setProteinG(45);
        request.setCarbsG(60);
        request.setFatG(12);

        when(dietEntryRepository.findByIdAndUserId(entryId, userId)).thenReturn(Optional.of(entry));
        when(dietEntryRepository.save(any(DietEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DietEntryResponse response = dietService.updateEntry(userId, entryId, request);

        assertThat(response.getFoodName()).isEqualTo("Chicken and rice");
        assertThat(response.getCalories()).isEqualTo(650);
        assertThat(entry.getProteinG()).isEqualTo(45);
    }
}
