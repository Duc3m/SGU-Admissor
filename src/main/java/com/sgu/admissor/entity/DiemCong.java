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
@Table(name = "diemcong")
public class DiemCong {
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "matohop",
            referencedColumnName = "matohop"
    )
    private ToHop toHop;
    
    @Column(name = "phuongthuc", length = 45)
    private String phuongThuc;
    
    @Column(name = "diemCC", precision = 6, scale = 2)
    private BigDecimal diemCc;
    @Column(name = "diemUtxt", precision = 6, scale = 2)
    private BigDecimal diemUtxt;
    @Column(name = "diemTong", precision = 6, scale = 2)
    private BigDecimal diemTong;
    
    @Column(name = "ghichu")
    private String ghiChu;
    
    @Column(name = "dc_key")
    private String dcKey;

}
