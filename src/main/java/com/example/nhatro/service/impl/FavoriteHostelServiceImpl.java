package com.example.nhatro.service.impl;

import com.example.nhatro.dto.response.FavoriteResponseDTO;
import com.example.nhatro.entity.FavoriteHostel;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.repository.FavoriteHostelRepository;
import com.example.nhatro.repository.HostelRepository;
import com.example.nhatro.service.FavoriteHostelService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteHostelServiceImpl implements FavoriteHostelService {
    @Autowired
    private FavoriteHostelRepository favoriteHostelRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Override
    @Transactional
    public FavoriteResponseDTO addFavorite(Long Id, Long hostelId) {
       if(favoriteHostelRepository.existsByUserIdAndHostelId(Id, hostelId)){return null;}
            FavoriteHostel favorite = new FavoriteHostel(null, Id, hostelId);
            FavoriteHostel savedFavorite = favoriteHostelRepository.save(favorite);
            return new FavoriteResponseDTO(
                savedFavorite.getId(),
                savedFavorite.getUserId(),
                savedFavorite.getHostelId()
                
            );
    }

    @Override
    @Transactional
    public void removeFavorite(Long Id, Long hostelId) {
        favoriteHostelRepository.deleteByUserIdAndHostelId(Id, hostelId);
    }

    @Override
    public List<FavoriteResponseDTO> getFavoritesByUser(Long Id) {
        List<FavoriteHostel> favorites = favoriteHostelRepository.findByUserId(Id);
        return favorites.stream()
            .map(fav -> {
                Hostel hostel = hostelRepository.findById(fav.getHostelId()).orElse(null);
                return new FavoriteResponseDTO(
                    fav.getId(),
                    fav.getUserId(),
                    fav.getHostelId()
                   
                );
            })
            .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorite(Long Id, Long hostelId) {
        return favoriteHostelRepository.existsByUserIdAndHostelId(Id, hostelId);
    }
}
