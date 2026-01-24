package com.example.nhatro.repository;

import com.example.nhatro.entity.FavoriteHostel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteHostelRepository extends JpaRepository<FavoriteHostel, Long> {
    List<FavoriteHostel> findByUserId(Long Id);
    void deleteByUserIdAndHostelId(Long Id, Long hostelId);
    boolean existsByUserIdAndHostelId(Long Id, Long hostelId);
}
