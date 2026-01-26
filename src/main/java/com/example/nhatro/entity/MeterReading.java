package com.example.nhatro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "meter_readings")
@Data
@EqualsAndHashCode(callSuper = true)
public class MeterReading extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long readingId;


    private LocalDate recordDate; // Ngày ghi
    
    private Integer elecIndex; // Chỉ số điện mới
    private Integer waterIndex; // Chỉ số nước mới
    
    private String imageUrl; // Link ảnh chụp đồng hồ
}