package com.example.nhatro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tenants")

public class Tenant extends BaseEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long tenantId;

   //khoá ngoại liên kết với bảng User
   @OneToOne@JoinColumn
   (name = "user_id", referencedColumnName = "id", unique = true) //referencedColumnName la khoa chinh cua bang User
   private User user;

   private String cccd;
   private String hometown;
   private String phone;
    
}
