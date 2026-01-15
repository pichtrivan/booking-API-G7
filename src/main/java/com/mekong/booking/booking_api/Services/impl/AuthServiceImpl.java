package com.mekong.booking.booking_api.Services.impl;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.management.relation.RoleNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.mekong.booking.booking_api.Dtos.Request.RegisterRequest;
import com.mekong.booking.booking_api.Dtos.Response.RegisterResponse;
import com.mekong.booking.booking_api.Entity.UserRole;
import com.mekong.booking.booking_api.Entity.Role;
import com.mekong.booking.booking_api.Entity.User;
import com.mekong.booking.booking_api.Exception.ConflictException;
import com.mekong.booking.booking_api.Utils.Validation;
import com.mekong.booking.booking_api.Repository.RoleRepository;
import com.mekong.booking.booking_api.Repository.UserRepository;
import com.mekong.booking.booking_api.Repository.UserRoleRepository;
import com.mekong.booking.booking_api.Security.JwtUtils;
import com.mekong.booking.booking_api.Services.AuthService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


// 1 . register
    @Override
    public RegisterResponse register(RegisterRequest request) {
         Validation.validateRegister(request);

         if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
                            throw new ConflictException("Username or email already exists");
         }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new ArrayList<>());
        userRepository.save(user);

        Role userRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RoleNotFoundException("CUSTOMER role not found"));
        assignRole(user, userRole);

        if (request.isServiceProvider()) {
            Role agentRole = roleRepository.findByName("ServiceProvider")
                    .orElseThrow(() -> new RoleNotFoundException("ServiceProvider role not found"));
            assignRole(user, agentRole);
        }

        List<String> roles = user.getRoles().stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .collect(Collectors.toList());

        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                roles
        );
    }


    private void assignRole(User user, Role userRole) {
        
        throw new UnsupportedOperationException("Unimplemented method 'assignRole'");
    }
}
    