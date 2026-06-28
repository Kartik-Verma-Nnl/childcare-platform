package net.kartikverma.childcare.service;

import net.kartikverma.childcare.dto.request.BookingRequest;
import net.kartikverma.childcare.enums.BookingStatus;
import net.kartikverma.childcare.kafka.producer.BookingEventProducer;
import net.kartikverma.childcare.model.AvailabilitySlot;
import net.kartikverma.childcare.model.CaregiverProfile;
import net.kartikverma.childcare.model.User;
import net.kartikverma.childcare.repository.AvailabilitySlotRepository;
import net.kartikverma.childcare.repository.BookingRepository;
import net.kartikverma.childcare.repository.CaregiverRepository;
import net.kartikverma.childcare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CaregiverRepository caregiverRepository;

    @Mock
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Mock
    private BookingEventProducer bookingEventProducer;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private BookingService bookingService;

    private User parent;
    private User caregiverUser;
    private CaregiverProfile caregiverProfile;
    private AvailabilitySlot slot;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        parent = User.builder()
                .id(1L)
                .name("Rahul Verma")
                .email("rahul@gmail.com")
                .build();

        caregiverUser = User.builder()
                .id(2L)
                .name("Priya Sharma")
                .email("priya@gmail.com")
                .build();

        caregiverProfile = CaregiverProfile.builder()
                .id(1L)
                .user(caregiverUser)
                .hourlyRates(BigDecimal.valueOf(250))
                .build();

        slot = AvailabilitySlot.builder()
                .id(1L)
                .caregiver(caregiverProfile)
                .slotDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(18, 0))
                .endTime(LocalTime.of(22, 0))
                .isBooked(false)
                .build();

        bookingRequest = new BookingRequest();
        bookingRequest.setCaregiverId(1L);
        bookingRequest.setSlotId(1L);
        bookingRequest.setNotes("Test booking");
    }

    @Test
    void createBooking_shouldSucceed_whenSlotIsAvailable() {
        // Arrange
        when(userRepository.findByEmail("rahul@gmail.com")).thenReturn(Optional.of(parent));
        when(caregiverRepository.findById(1L)).thenReturn(Optional.of(caregiverProfile));
        when(availabilitySlotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(bookingRepository.existsBySlotIdAndStatusNot(1L, BookingStatus.CANCELLED))
                .thenReturn(false);

        // Act
        var response = bookingService.createBooking("rahul@gmail.com", bookingRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(response.getTotalAmount()).isEqualTo(BigDecimal.valueOf(1000)); // 4 hours * 250

        // Verify slot was locked
        verify(availabilitySlotRepository).save(argThat(savedSlot -> savedSlot.getIsBooked()));

        // Verify Kafka event was published
        verify(bookingEventProducer).publishBookingEvent(any());
    }

    @Test
    void createBooking_shouldThrow_whenSlotAlreadyBooked() {
        // Arrange
        slot.setIsBooked(true);

        when(userRepository.findByEmail("rahul@gmail.com")).thenReturn(Optional.of(parent));
        when(caregiverRepository.findById(1L)).thenReturn(Optional.of(caregiverProfile));
        when(availabilitySlotRepository.findById(1L)).thenReturn(Optional.of(slot));

        // Act + Assert
        assertThatThrownBy(() ->
                bookingService.createBooking("rahul@gmail.com", bookingRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already booked");

        // Verify booking was never saved
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_shouldThrow_whenSlotBelongsToDifferentCaregiver() {
        // Arrange
        CaregiverProfile differentCaregiver = CaregiverProfile.builder().id(99L).build();
        slot.setCaregiver(differentCaregiver);

        when(userRepository.findByEmail("rahul@gmail.com")).thenReturn(Optional.of(parent));
        when(caregiverRepository.findById(1L)).thenReturn(Optional.of(caregiverProfile));
        when(availabilitySlotRepository.findById(1L)).thenReturn(Optional.of(slot));

        // Act + Assert
        assertThatThrownBy(() ->
                bookingService.createBooking("rahul@gmail.com", bookingRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    void cancelBooking_shouldReleaseSlot_whenCancelledByParent() {
        // Arrange
        var booking = net.kartikverma.childcare.model.Booking.builder()
                .id(1L)
                .parent(parent)
                .caregiver(caregiverProfile)
                .slot(slot)
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Act
        var response = bookingService.cancelBooking("rahul@gmail.com", 1L);

        // Assert
        assertThat(response.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(availabilitySlotRepository).save(argThat(s -> !s.getIsBooked()));
    }

    @Test
    void cancelBooking_shouldThrow_whenUnauthorizedUserTriesToCancel() {
        // Arrange
        var booking = net.kartikverma.childcare.model.Booking.builder()
                .id(1L)
                .parent(parent)
                .caregiver(caregiverProfile)
                .slot(slot)
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Act + Assert — some random unrelated user tries to cancel
        assertThatThrownBy(() ->
                bookingService.cancelBooking("stranger@gmail.com", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }
}