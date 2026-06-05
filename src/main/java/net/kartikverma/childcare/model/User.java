package net.kartikverma.childcare.model;

import jakarta.persistence.*;
import lombok.*;
import net.kartikverma.childcare.enums.Role;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @jakarta.persistence.Id
    private Long id1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    private LocalDateTime creationAt;

    //Relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private CaregiverProfile caregiverProfile;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
