package com.example.nhatro.service;

import com.example.nhatro.dto.response.FavoriteResponseDTO;
import com.example.nhatro.entity.FavoriteHostel;
import java.util.List;

public interface FavoriteHostelService {
    FavoriteResponseDTO addFavorite(Long Id, Long hostelId);
    void removeFavorite(Long Id, Long hostelId);
    List<FavoriteResponseDTO> getFavoritesByUser(Long Id);
    boolean isFavorite(Long Id, Long hostelId);
}
