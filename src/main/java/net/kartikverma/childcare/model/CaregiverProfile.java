package net.kartikverma.childcare.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "caregiver_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CaregiverProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String bio;

    @Column(precision = 6,scale = 2)
    private BigDecimal hourlyRates;

    private Integer experienceYears;

    private String specializations;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isVerified = false;

    private String docURl;

    private String city;

    @Builder.Default
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    //Relationships
    @OneToMany(mappedBy = "caregiver", cascade = CascadeType.ALL)
    private List<AvailabilitySlot> avialabilitySlots;

    @OneToMany(mappedBy = "caregiver", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "caregiver", cascade = CascadeType.ALL)
    private List<Review> reviews;
}
