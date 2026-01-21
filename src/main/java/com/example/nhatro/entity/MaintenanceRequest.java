package com.example.nhatro.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "maintenance_requests")
@Data
@EqualsAndHashCode(callSuper = true)
public class MaintenanceRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant; // Ai báo?

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String imageUrls; // Có thể lưu dạng JSON String ["url1", "url2"]

    private String status; // PENDING, PROCESSING, DONE
    private BigDecimal cost; // Chi phí sửa chữa (nếu có)
}