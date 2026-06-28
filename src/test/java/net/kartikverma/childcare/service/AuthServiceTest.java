package net.kartikverma.childcare.service;

import net.kartikverma.childcare.dto.request.LoginRequest;
import net.kartikverma.childcare.dto.request.RegisterRequest;
import net.kartikverma.childcare.enums.Role;
import net.kartikverma.childcare.model.User;
import net.kartikverma.childcare.repository.UserRepository;
import net.kartikverma.childcare.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Kartik Verma");
        registerRequest.setEmail("kartik@gmail.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("9876543210");
        registerRequest.setRole(Role.PARENT);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("kartik@gmail.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_shouldSucceed_whenEmailNotAlreadyTaken() {
        // Arrange
        when(userRepository.existsByEmail("kartik@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userDetailsService.loadUserByUsername("kartik@gmail.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("fake-jwt-token");

        // Act
        var response = authService.register(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getEmail()).isEqualTo("kartik@gmail.com");
        assertThat(response.getRole()).isEqualTo(Role.PARENT);

        // Verify the saved user had the password encoded, not plain text
        verify(userRepository).save(argThat(user ->
                user.getPassword().equals("encoded_password")));
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail("kartik@gmail.com")).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already registered");

        // Verify no user was ever saved
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldSucceed_whenCredentialsAreValid() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .name("Kartik Verma")
                .email("kartik@gmail.com")
                .role(Role.PARENT)
                .build();

        when(userRepository.findByEmail("kartik@gmail.com")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("kartik@gmail.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("fake-jwt-token");

        // Act
        var response = authService.login(loginRequest);

        // Assert
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getName()).isEqualTo("Kartik Verma");

        // Verify authentication was actually attempted
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_shouldThrow_whenUserNotFoundAfterAuthentication() {
        // Arrange — authentication succeeds but user lookup fails (edge case)
        when(userRepository.findByEmail("kartik@gmail.com")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }
}