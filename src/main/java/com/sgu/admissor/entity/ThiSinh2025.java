/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Admin
 */

@NoArgsConstructor
@Data
@Entity
@Table(name = "thisinh2025")
public class ThiSinh2025 {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "cccd", length = 20)
    private String cccd;
    
    @Column(name = "sobaodanh", length = 45)
    private String soBaoDanh;
    
    @Column(name = "hoten", length = 100)
    private String hoTen;
    
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    
    @Column(name = "dien_thoai", length = 20)
    private String dienThoai;
    
    @Column(name = "password", length = 100)
    private String password;
    
    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "noi_sinh", length = 45)
    private String noiSinh;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @Column(name = "doi_tuong", length = 45)
    private String doiTuong;
    
    @Column(name = "khu_vuc", length = 45)
    private String khuVuc;
    
}
