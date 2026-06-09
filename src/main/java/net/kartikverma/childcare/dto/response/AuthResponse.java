package net.kartikverma.childcare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kartikverma.childcare.enums.Role;

@Getter @Setter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String name;
    private Role role;
}
