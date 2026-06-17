package net.kartikverma.childcare.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.kartikverma.childcare.enums.Role;

@Getter @Setter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
}
