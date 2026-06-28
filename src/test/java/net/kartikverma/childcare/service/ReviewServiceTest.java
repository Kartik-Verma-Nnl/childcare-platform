package net.kartikverma.childcare.service;

import net.kartikverma.childcare.dto.request.ReviewRequest;
import net.kartikverma.childcare.enums.BookingStatus;
import net.kartikverma.childcare.model.Booking;
import net.kartikverma.childcare.model.CaregiverProfile;
import net.kartikverma.childcare.model.User;
import net.kartikverma.childcare.repository.BookingRepository;
import net.kartikverma.childcare.repository.CaregiverRepository;
import net.kartikverma.childcare.repository.ReviewRepository;
import net.kartikverma.childcare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CaregiverRepository caregiverRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User parent;
    private CaregiverProfile caregiverProfile;
    private Booking completedBooking;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        parent = User.builder().id(1L).name("Rahul Verma").email("rahul@gmail.com").build();

        User caregiverUser = User.builder().id(2L).name("Priya Sharma").build();
        caregiverProfile = CaregiverProfile.builder().id(1L).user(caregiverUser).build();

        completedBooking = Booking.builder()
                .id(1L)
                .parent(parent)
                .caregiver(caregiverProfile)
                .status(BookingStatus.COMPLETED)
                .build();

        reviewRequest = new ReviewRequest();
        reviewRequest.setBookingId(1L);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Excellent caregiver!");
    }

    @Test
    void submitReview_shouldSucceed_whenBookingIsCompletedAndUnreviewed() {
        // Arrange
        when(userRepository.findByEmail("rahul@gmail.com")).thenReturn(Optional.of(parent));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(completedBooking));
        when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.empty());
        when(reviewRepository.findAverageRatingByCaregiverID(1L)).thenReturn(5.0);

        // Act
        var response = reviewService.submitReview("rahul@gmail.com", reviewRequest);

        // Assert
        assertThat(response.getRating()).isEqualTo(5);
        verify(reviewRepository).save(any());

        // Verify caregiver's average rating got updated
        verify(caregiverRepository).save(argThat(profile ->
                profile.getAverageRating().compareTo(BigDecimal.valueOf(5.0)) == 0));
    }

    @Test
    void submitReview_shouldThrow_whenBookingNotCompleted() {
        // Arrange
        completedBooking.setStatus(BookingStatus.CONFIRMED); // not completed yet

        when(userRepository.findByEmail("rahul@gmail.com")).thenReturn(Optional.of(parent));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(completedBooking));

        // Act + Assert
        assertThatThrownBy(() -> reviewService.submitReview("rahul@gmail.com", reviewRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("completed");

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void submitReview_shouldThrow_whenReviewAlreadyExistsForBooking() {
        // Arrange
        when(userRepository.findByEmail("rahul@gmail.com")).thenReturn(Optional.of(parent));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(completedBooking));
        when(reviewRepository.findByBookingId(1L))
                .thenReturn(Optional.of(net.kartikverma.childcare.model.Review.builder().id(99L).build()));

        // Act + Assert
        assertThatThrownBy(() -> reviewService.submitReview("rahul@gmail.com", reviewRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already reviewed");
    }

    @Test
    void submitReview_shouldThrow_whenParentDoesNotOwnBooking() {
        // Arrange — a different parent tries to review someone else's booking
        User stranger = User.builder().id(99L).email("stranger@gmail.com").build();

        when(userRepository.findByEmail("stranger@gmail.com")).thenReturn(Optional.of(stranger));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(completedBooking));

        // Act + Assert
        assertThatThrownBy(() -> reviewService.submitReview("stranger@gmail.com", reviewRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("own bookings");
    }
}