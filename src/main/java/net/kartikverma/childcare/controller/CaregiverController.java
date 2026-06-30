package net.kartikverma.childcare.controller;

import jakarta.validation.Valid;
import net.kartikverma.childcare.dto.request.AvailabilityRequest;
import net.kartikverma.childcare.dto.request.CaregiverProfileRequest;
import net.kartikverma.childcare.dto.response.AvailabilitySlotResponse;
import net.kartikverma.childcare.dto.response.CaregiverResponse;
import net.kartikverma.childcare.dto.response.PagedResponse;
import net.kartikverma.childcare.service.CaregiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/caregiver")
public class CaregiverController {

    @Autowired
    private CaregiverService caregiverService;

    // Public — all verified caregivers
    @GetMapping("/all")
    public ResponseEntity<List<CaregiverResponse>> getAllCaregivers() {
        return ResponseEntity.ok(caregiverService.getAllVerifiedCaregivers());
    }

    // Paginated version — preferred for production use
    @GetMapping
    public ResponseEntity<PagedResponse<CaregiverResponse>> getAllCaregiversPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(caregiverService.getAllVerifiedCaregiversPaged(page, size));
    }

    // Public — single caregiver
    @GetMapping("/{id}")
    public ResponseEntity<CaregiverResponse> getCaregiverById(@PathVariable Long id) {
        return ResponseEntity.ok(caregiverService.getCaregiverById(id));
    }

    // Caregiver — update own profile (JWT required)
    @PutMapping("/profile")
    public ResponseEntity<CaregiverResponse> updateProfile(
            @RequestBody CaregiverProfileRequest request,
            Principal principal) {
        return ResponseEntity.ok(
                caregiverService.updateProfile(principal.getName(), request));
    }

    // Caregiver — add availability slot (JWT required)
    @PostMapping("/slots")
    public ResponseEntity<AvailabilitySlotResponse> addSlot(
            @Valid @RequestBody AvailabilityRequest request,
            Principal principal) {
        return ResponseEntity.ok(
                caregiverService.addSlot(principal.getName(), request));
    }

    // Caregiver — view own slots (JWT required)
    @GetMapping("/slots/my")
    public ResponseEntity<List<AvailabilitySlotResponse>> getMySlots(Principal principal) {
        return ResponseEntity.ok(caregiverService.getMySlots(principal.getName()));
    }

    // Caregiver — delete a slot (JWT required)
    @DeleteMapping("/slots/{id}")
    public ResponseEntity<String> deleteSlot(
            @PathVariable Long id,
            Principal principal) {
        caregiverService.deleteSlot(principal.getName(), id);
        return ResponseEntity.ok("Slot deleted successfully");
    }
}