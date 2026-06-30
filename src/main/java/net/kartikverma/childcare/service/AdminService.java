package net.kartikverma.childcare.service;

import net.kartikverma.childcare.dto.response.BookingResponse;
import net.kartikverma.childcare.dto.response.CaregiverResponse;
import net.kartikverma.childcare.dto.response.PagedResponse;
import net.kartikverma.childcare.dto.response.UserResponse;
import net.kartikverma.childcare.exception.ResourceNotfoundException;
import net.kartikverma.childcare.model.CaregiverProfile;
import net.kartikverma.childcare.model.User;
import net.kartikverma.childcare.repository.BookingRepository;
import net.kartikverma.childcare.repository.CaregiverRepository;
import net.kartikverma.childcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private CaregiverRepository caregiverRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all unverified caregivers
    public List<CaregiverResponse> getUnverifiedCaregivers() {
        return caregiverRepository.findByIsVerifiedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Verify a caregiver
    @CacheEvict(value = "caregivers", allEntries = true)
    public CaregiverResponse verifyCaregiver(Long caregiverId) {
        CaregiverProfile caregiver = caregiverRepository.findById(caregiverId)
                .orElseThrow(() -> new ResourceNotfoundException("Caregiver not found"));

        if (caregiver.getIsVerified()) {
            throw new RuntimeException("Caregiver is already verified");
        }

        caregiver.setIsVerified(true);
        caregiverRepository.save(caregiver);
        return mapToResponse(caregiver);
    }

    // Unverify a caregiver (revoke verification)
    @CacheEvict(value = "caregivers", allEntries = true)
    public CaregiverResponse unverifyCaregiver(Long caregiverId) {
        CaregiverProfile caregiver = caregiverRepository.findById(caregiverId)
                .orElseThrow(() -> new ResourceNotfoundException("Caregiver not found"));

        caregiver.setIsVerified(false);
        caregiverRepository.save(caregiver);
        return mapToResponse(caregiver);
    }

    // Get all bookings on platform
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    public PagedResponse<BookingResponse> getAllBookingsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> responsePage = bookingRepository
                .findAll(pageable)
                .map(this::mapToBookingResponse);

        return PagedResponse.from(responsePage);
    }

    public PagedResponse<UserResponse> getAllUsersPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> responsePage = userRepository
                .findAll(pageable)
                .map(this::mapToUserResponse);

        return PagedResponse.from(responsePage);
    }

    // Get all users
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // Map caregiver to response
    private CaregiverResponse mapToResponse(CaregiverProfile profile) {
        return CaregiverResponse.builder()
                .id(profile.getId())
                .name(profile.getUser().getName())
                .email(profile.getUser().getEmail())
                .bio(profile.getBio())
                .hourlyRate(profile.getHourlyRates())
                .experienceYears(profile.getExperienceYears())
                .specializations(profile.getSpecializations())
                .city(profile.getCity())
                .isVerified(profile.getIsVerified())
                .averageRating(profile.getAverageRating())
                .build();
    }

    // Map booking to response
    private BookingResponse mapToBookingResponse(
            net.kartikverma.childcare.model.Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .parentName(booking.getParent().getName())
                .parentEmail(booking.getParent().getEmail())
                .caregiverName(booking.getCaregiver().getUser().getName())
                .caregiverEmail(booking.getCaregiver().getUser().getEmail())
                .slotDate(booking.getSlot().getSlotDate())
                .startTime(booking.getSlot().getStartTime())
                .endTime(booking.getSlot().getEndTime())
                .status(booking.getStatus())
                .durationHours(booking.getDurationHours())
                .totalAmount(booking.getTotalAmount())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    // Map user to response
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }
}