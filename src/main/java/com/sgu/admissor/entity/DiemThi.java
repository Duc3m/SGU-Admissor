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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
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
@Table(name = "diemthi")
public class DiemThi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "cccd", referencedColumnName = "cccd"),
            @JoinColumn(name = "sobaodanh", referencedColumnName = "sobaodanh")
    })
    private ThiSinh2025 thiSinh;
    
    @Column(name = "phuongthuc", length = 10)
    private String phuongThuc;
    
    @Column(name = "TO", precision = 8, scale = 2)
    private BigDecimal to;
    
    @Column(name = "LI", precision = 8, scale = 2)
    private BigDecimal li;
    
    @Column(name = "HO", precision = 8, scale = 2)
    private BigDecimal ho;
    
    @Column(name = "SI", precision = 8, scale = 2)
    private BigDecimal si;
    
    @Column(name = "SU", precision = 8, scale = 2)
    private BigDecimal su;
    
    @Column(name = "DI", precision = 8, scale = 2)
    private BigDecimal di;
    
    @Column(name = "VA", precision = 8, scale = 2)
    private BigDecimal va;
    
    @Column(name = "N1_THI", precision = 8, scale = 2)
    private BigDecimal n1Thi;
    
    @Column(name = "N1_CC", precision = 8, scale = 2)
    private BigDecimal n1Cc;
    
    @Column(name = "CNCN", precision = 8, scale = 2)
    private BigDecimal cncn;
    
    @Column(name = "CNNN", precision = 8, scale = 2)
    private BigDecimal cnnn;
    
    @Column(name = "TI", precision = 8, scale = 2)
    private BigDecimal ti;
    
    @Column(name = "KTPL", precision = 8, scale = 2)
    private BigDecimal ktpl;
    
    @Column(name = "NL1", precision = 8, scale = 2)
    private BigDecimal nl1;
    
    @Column(name = "NK1", precision = 8, scale = 2)
    private BigDecimal nk1;
    
    @Column(name = "NK2", precision = 8, scale = 2)
    private BigDecimal nk2;

}
