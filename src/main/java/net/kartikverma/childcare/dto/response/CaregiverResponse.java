package net.kartikverma.childcare.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaregiverResponse {

    private Long id;
    private String name;
    private String email;
    private String bio;
    private BigDecimal hourlyRate;
    private Integer experienceYears;
    private String specializations;
    private String city;
    private Boolean isVerified;
    private BigDecimal averageRating;
}