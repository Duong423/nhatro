package com.example.nhatro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_members")
@Data
public class RoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    private String fullName;
    private String phone;
    private String cccd;
    
    private Boolean isRegisteredTemp; // Đã đăng ký tạm trú chưa
}