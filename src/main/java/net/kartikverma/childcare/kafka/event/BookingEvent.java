package net.kartikverma.childcare.kafka.event;

import lombok.*;
import net.kartikverma.childcare.enums.BookingStatus;
import java.io.Serializable;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class BookingEvent implements Serializable {

    private Long bookingId;
    private String parentEmail;
    private String parentName;
    private String caregiverEmail;
    private String caregiverName;
    private BookingStatus status;
    private String message;
}
