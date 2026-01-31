package com.example.nhatro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor   
@Table(name = "tenants")

public class Tenant extends BaseEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long tenantId;

   //khoá ngoại liên kết với bảng User
   @OneToOne@JoinColumn
   (name = "user_id", referencedColumnName = "id", unique = true) //referencedColumnName la khoa chinh cua bang User
   private User user;

   @jakarta.persistence.Column(name = "name", length = 100)
   private String name;

   private String cccd;
   private String hometown;
   private String phone;

   @jakarta.persistence.Column(name = "email", length = 100)
   private String email;
    
}
