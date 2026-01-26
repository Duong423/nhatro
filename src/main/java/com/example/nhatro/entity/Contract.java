package com.example.nhatro.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.nhatro.enums.ContractStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Contract extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant; // Người đại diện ký

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal deposit; // Tiền cọc

    // Lưu cứng giá điện nước cho HĐ này (để sau này giá thị trường tăng cũng ko ảnh hưởng HĐ cũ)
    private BigDecimal elecUnitPrice;
    private BigDecimal waterUnitPrice;
    
    private Integer billingCycleDay; // Ngày chốt tiền hàng tháng (VD: 15)

    @Enumerated(EnumType.STRING)
    private ContractStatus status; // ACTIVE, EXPIRED...

    // Danh sách người ở ghép
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<RoomMember> members;

    // Dịch vụ khách đăng ký dùng
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ContractService> contractServices;
}