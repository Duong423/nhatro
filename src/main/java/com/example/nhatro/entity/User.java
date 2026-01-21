package com.example.nhatro.entity;

import com.example.nhatro.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name ="users")
@Data
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;

    @Column(unique =true) // gia tri email la duy nhat 
    private String email;
    private String phone;
    private String password;
    private String avatarUrl;
    
    @Column(length = 1000)
    private String accessToken;
    
    @Column(length = 1000)
    private String refreshToken;
    
	@Enumerated(EnumType.STRING)
    private UserRole role;
    // private String status; // ACTIVE, INACTIVE
 
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL) //CascadeType.ALL nghĩa là mọi thao tác trên entity User cũng sẽ tự động thực hiện trên entity Tenant liên kết. Ví dụ: khi xóa User thì Tenant liên kết cũng bị xóa theo.
    @JsonIgnore
    private Tenant tenantProfile;
    
    
}
