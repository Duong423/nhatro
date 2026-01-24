package com.example.nhatro.controller;

import com.example.nhatro.common.dto.response.ApiResponse;
import com.example.nhatro.dto.response.FavoriteResponseDTO;
import com.example.nhatro.entity.User;
import com.example.nhatro.repository.UserRepository;
import com.example.nhatro.service.FavoriteHostelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
@RestController
@RequestMapping("/api/favorites")
public class FavoriteHostelController {
    @Autowired
    private FavoriteHostelService favoriteHostelService;
    
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAnyRole('TENANT')")
    @PostMapping
    public ApiResponse<FavoriteResponseDTO> addFavorite(@RequestParam Long hostelId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        FavoriteResponseDTO favorite = favoriteHostelService.addFavorite(user.getId(), hostelId);
      try {
            return ApiResponse.<FavoriteResponseDTO>builder()
                    .code(201)
                    .message("Added to favorites successfully")
                    .result(favorite)
                    .build();
        } catch(RuntimeException e) {
            throw new RuntimeException("Hostel is already in favorites");
        }
    }

    @PreAuthorize("hasAnyRole('TENANT')")
    @DeleteMapping
    public ApiResponse<Void> removeFavorite(@RequestParam Long hostelId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        favoriteHostelService.removeFavorite(user.getId(), hostelId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Removed from favorites successfully")
                .build();
    }

    @PreAuthorize("hasAnyRole('TENANT')")
    @GetMapping
    public ApiResponse<List<FavoriteResponseDTO>> getFavorites(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<FavoriteResponseDTO> favorites = favoriteHostelService.getFavoritesByUser(user.getId());
        return ApiResponse.<List<FavoriteResponseDTO>>builder()
                .code(200)
                .message("Get favorites successfully")
                .result(favorites)
                .build();
    }

    @PreAuthorize("hasAnyRole('TENANT')")
    @GetMapping("/check")
    public ApiResponse<Boolean> isFavorite(@RequestParam Long hostelId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean result = favoriteHostelService.isFavorite(user.getId(), hostelId);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message("Check favorite status successfully")
                .result(result)
                .build();
    }
}
