package net.kartikverma.childcare.service;

import net.kartikverma.childcare.dto.request.AvailabilityRequest;
import net.kartikverma.childcare.exception.ResourceNotfoundException;
import net.kartikverma.childcare.model.CaregiverProfile;
import net.kartikverma.childcare.model.User;
import net.kartikverma.childcare.repository.AvailabilitySlotRepository;
import net.kartikverma.childcare.repository.CaregiverRepository;
import net.kartikverma.childcare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaregiverServiceTest {

    @Mock
    private CaregiverRepository caregiverRepository;

    @Mock
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CaregiverService caregiverService;

    private User caregiverUser;
    private CaregiverProfile caregiverProfile;

    @BeforeEach
    void setUp() {
        caregiverUser = User.builder().id(1L).email("priya@gmail.com").build();
        caregiverProfile = CaregiverProfile.builder().id(1L).user(caregiverUser).build();
    }

    @Test
    void addSlot_shouldThrow_whenStartTimeAfterEndTime() {
        // Arrange
        when(userRepository.findByEmail("priya@gmail.com")).thenReturn(Optional.of(caregiverUser));
        when(caregiverRepository.findByUserId(1L)).thenReturn(Optional.of(caregiverProfile));

        AvailabilityRequest request = new AvailabilityRequest();
        request.setSlotDate(LocalDate.now().plusDays(1));
        request.setStartTime(LocalTime.of(22, 0));  // 10 PM
        request.setEndTime(LocalTime.of(18, 0));     // 6 PM — before start!

        // Act + Assert
        assertThatThrownBy(() -> caregiverService.addSlot("priya@gmail.com", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Start time cannot be after end time");
    }

    @Test
    void addSlot_shouldThrow_whenDateIsInThePast() {
        // Arrange
        when(userRepository.findByEmail("priya@gmail.com")).thenReturn(Optional.of(caregiverUser));
        when(caregiverRepository.findByUserId(1L)).thenReturn(Optional.of(caregiverProfile));

        AvailabilityRequest request = new AvailabilityRequest();
        request.setSlotDate(LocalDate.now().minusDays(1)); // yesterday
        request.setStartTime(LocalTime.of(18, 0));
        request.setEndTime(LocalTime.of(22, 0));

        // Act + Assert
        assertThatThrownBy(() -> caregiverService.addSlot("priya@gmail.com", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("past");
    }

    @Test
    void getCaregiverById_shouldThrow_whenCaregiverDoesNotExist() {
        // Arrange
        when(caregiverRepository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> caregiverService.getCaregiverById(999L))
                .isInstanceOf(ResourceNotfoundException.class)
                .hasMessageContaining("not found");
    }
}