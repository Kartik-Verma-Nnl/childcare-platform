package net.kartikverma.childcare.controller;

import net.kartikverma.childcare.dto.response.BookingResponse;
import net.kartikverma.childcare.dto.response.CaregiverResponse;
import net.kartikverma.childcare.dto.response.PagedResponse;
import net.kartikverma.childcare.dto.response.UserResponse;
import net.kartikverma.childcare.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Get all unverified caregivers
    @GetMapping("/caregivers/unverified")
    public ResponseEntity<List<CaregiverResponse>> getUnverifiedCaregivers() {
        return ResponseEntity.ok(adminService.getUnverifiedCaregivers());
    }

    // Verify a caregiver
    @PutMapping("/caregivers/{id}/verify")
    public ResponseEntity<CaregiverResponse> verifyCaregiver(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.verifyCaregiver(id));
    }

    // Revoke caregiver verification
    @PutMapping("/caregivers/{id}/unverify")
    public ResponseEntity<CaregiverResponse> unverifyCaregiver(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.unverifyCaregiver(id));
    }

    // Get all bookings
    @GetMapping("/bookings/all")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(adminService.getAllBookings());
    }

    // Get all users
    @GetMapping("/users/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/bookings")
    public ResponseEntity<PagedResponse<BookingResponse>> getAllBookingsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllBookingsPaged(page, size));
    }

    @GetMapping("/users")
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllUsersPaged(page, size));
    }
}