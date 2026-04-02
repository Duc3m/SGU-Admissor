/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Admin
 */

@NoArgsConstructor
@Data
@Entity
@Table(name = "nguyenvong")
public class NguyenVong {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cccd",
            referencedColumnName = "cccd"
    )
    private ThiSinh2025 thiSinh;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manganh",
            referencedColumnName = "manganh"
    )
    private Nganh nganh;
    
    @Column(name = "thutu")
    private Integer thuTu;
    
    @Column(name = "diem_thxt", precision = 10, scale = 5)
    private BigDecimal diemThxt;
    
    @Column(name = "diem_utqd", precision = 10, scale = 5)
    private BigDecimal diemUtqd;
    
    @Column(name = "diem_cong", precision = 6, scale = 2)
    private BigDecimal diemCong;
    
    @Column(name = "diem_xettuyen", precision = 10, scale = 5)
    private BigDecimal diemXetTuyen;
    
    @Column(name = "ketqua", length = 45)
    private String ketQua;
    
    @Column(name = "nv_key", length = 45)
    private String nvKey;
    
    @Column(name = "phuongthuc", length = 45)
    private String phuongThuc;
    
    @Column(name = "tohopmon", length = 45)
    private String toHopMon;

}
