package com.example.nhatro.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nhatro.entity.User;
import com.example.nhatro.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'TENANT')")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok("Tenant Dashboard - Tất cả user có thể xem");
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'TENANT')")
    public ResponseEntity<?> getRooms(Principal principal) {
        // Logic lấy phòng của tenant
        return ResponseEntity.ok("Phòng của tenant: " + principal.getName());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/contracts")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'TENANT')")
    public ResponseEntity<?> getContracts(Principal principal) {
        // Logic lấy hợp đồng của tenant
        return ResponseEntity.ok("Hợp đồng của tenant: " + principal.getName());
    }
}
