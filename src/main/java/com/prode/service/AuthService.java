package com.prode.service;

import com.prode.dto.AuthDto;
import com.prode.entity.User;
import com.prode.repository.UserRepository;
import com.prode.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .admin(false)
                .build();
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.isAdmin());
        return new AuthDto.AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.isAdmin());
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Email o contraseña incorrectos.");
        }
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.isAdmin());
        return new AuthDto.AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.isAdmin());
    }
}
